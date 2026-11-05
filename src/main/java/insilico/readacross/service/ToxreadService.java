package insilico.readacross.service;

import insilico.core.constant.InsilicoConstants;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.InvalidMoleculeException;
import insilico.core.exception.PropertyNotFoundException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.molecule.tools.Depiction;
import insilico.readacross.calculation.RaEngine;
import insilico.readacross.calculation.endpoints.*;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.local.DBConnectorLocal;
import insilico.readacross.database.objects.dbStructure;
import insilico.readacross.entity.*;
import insilico.readacross.exception.InvalidSmilesException;
import insilico.readacross.exception.RaException;
import insilico.readacross.gui.chart.components.interpolation.InterpolationDataPoint;
import insilico.readacross.type.interfaces.*;
import insilico.readacross.type.objects.RaAdditionalValue;
import insilico.readacross.type.objects.RaObject;
import insilico.readacross.type.objects.RaResultsContainer;
import insilico.readacross.utils.PrepareDatabase;
import lombok.extern.slf4j.Slf4j;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Slf4j
@Service
public class ToxreadService {

    @Value("${toxread.db.load_from_serialized_file}")
    private boolean LOAD_FROM_SERIALIZED_FILE;
    @Value("${toxread.db.db_file}")
    private String DB_FILE;
    private int imgWidth = 400;
    private int imgHeight = 300;

    private iDBConnector DBConn;

    public EndpointsResponse retrieveEndpoints() {
        return new EndpointsResponse(RaEngine.AVAILABLE_EP);
    }

    public RaResponse startRA(String smiles, String endpoint, int numSimilarMolecules)
            throws InvalidSmilesException, RaException {

        InsilicoMolecule mol = SmilesMolecule.Convert(smiles);
        if (!mol.IsValid()) {
           log.error("Unable to parse SMILES molecule {}, read-across not performed", smiles);
           throw new InvalidSmilesException("Unable to parse SMILES molecule" +
                   smiles + ", read-across not performed");
        }

        RaEngine raEngine = openDB();
        raEngine.setSimilarMoleculesSize(numSimilarMolecules);

        RaEndpoint raEndpoint = selectRaEndpoint(endpoint);

        RaResultsContainer res;
        try {
            res = raEngine.Calculate(raEndpoint, mol);
        } catch (Exception exception) {
            log.error("Error while calculating endpoint {} - {}", endpoint, exception.getMessage());
            throw new RaException("An error has occurred while computing endpoint " + endpoint);
        }

        return parseResults(res);
    }

    private RaEngine openDB() throws RaException {
        try {
            if (LOAD_FROM_SERIALIZED_FILE) {
                this.DBConn = new DBConnectorLocal();
                HashMap<String, String> p = new HashMap<>();
                p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, DB_FILE);
                this.DBConn.Connect(p);
            } else {
                PrepareDatabase prepDB = new PrepareDatabase();
                prepDB.BuildDatabase();
                this.DBConn = prepDB.db;
            }
        } catch (Exception e) {
            log.info("Unable to open database");
            throw new RaException("Unable to open database");
        }

        return new RaEngine(this.DBConn);
    }

    private RaEndpoint selectRaEndpoint(String endpoint) throws RaException {
        RaEndpoint raEndpoint;
        try {
            switch (endpoint) {
                case RaEngine.EP_MUTAGENICITY:
                    raEndpoint = new RaEndpointMutagenicity(this.DBConn);
                    break;
                case RaEngine.EP_BCF:
                    raEndpoint = new RaEndpointBCF(this.DBConn);
                    break;
                case RaEngine.EP_ALGAE_COMBASE:
                    raEndpoint = new RaEndpointAlgaeCombase(this.DBConn);
                    break;
                case RaEngine.EP_MICROBES_COMBASE:
                    raEndpoint = new RaEndpointMicrobesCombase(this.DBConn);
                    break;
                case RaEngine.EP_DAPHNIA_COMBASE:
                    raEndpoint = new RaEndpointDaphniaCombase(this.DBConn);
                    break;
                case RaEngine.EP_FISH_COMBASE:
                    raEndpoint = new RaEndpointFishCombase(this.DBConn);
                    break;
                case RaEngine.EP_READY_BIO:
                    raEndpoint = new RaEndpointReadyBiodegradability(this.DBConn);
                    break;
                case RaEngine.EP_PERS_SOIL:
                    raEndpoint = new RaEndpointPersistenceSoil(this.DBConn);
                    break;
                case RaEngine.EP_PERS_WATER:
                    raEndpoint = new RaEndpointPersistenceWater(this.DBConn);
                    break;
                case RaEngine.EP_PERS_SEDIMENT:
                    raEndpoint = new RaEndpointPersistenceSediment(this.DBConn);
                    break;
                case RaEngine.EP_REPROTOX:
                    raEndpoint = new RaEndpointReprotox(this.DBConn);
                    break;
                case RaEngine.EP_HEPATOTOX:
                    raEndpoint = new RaEndpointHepatotox(this.DBConn);
                    break;
                case RaEngine.EP_NEPHROTOX:
                    raEndpoint = new RaEndpointNephrotox(this.DBConn);
                    break;
                case RaEngine.EP_SKIN:
                    raEndpoint = new RaEndpointSkinSensitization(this.DBConn);
                    break;
                case RaEngine.EP_FISH_VERMEER:
                    raEndpoint = new RaEndpointFishVermeer(this.DBConn);
                    break;
                case RaEngine.EP_DAPHNIA_VERMEER:
                    raEndpoint = new RaEndpointDaphniaVermeer(this.DBConn);
                    break;
                case RaEngine.EP_CGX_VERMEER:
                    raEndpoint = new RaEndpointCarcinogenicityCGX(this.DBConn);
                    break;
                case RaEngine.EP_MICRONUCLEUS_VERMEER:
                    raEndpoint = new RaEndpointMicronucleus(this.DBConn);
                    break;
                case RaEngine.EP_MICRONUCLEUS_INVIVO:
                    raEndpoint = new RaEndpointInvivoMicronucleus(this.DBConn);
                    break;
                case RaEngine.EP_ANDROGEN_RECEPTOR:
                    raEndpoint = new RaEndpointAndrogenReceptor(this.DBConn);
                    break;
                case RaEngine.EP_SKIN_IRRITATION:
                    raEndpoint = new RaEndpointSkinIrritation(this.DBConn);
                    break;
                case RaEngine.EP_AROMATASE:
                    raEndpoint = new RaEndpointAromatase(this.DBConn);
                    break;
                case RaEngine.EP_EYE_IRRITATION:
                    raEndpoint = new RaEndpointEyeIrritation(this.DBConn);
                    break;
                case RaEngine.EP_LD50:
                    raEndpoint = new RaEndpointLD50(this.DBConn);
                    break;
                case RaEngine.EP_REPROTOX_CONCERT:
                    raEndpoint = new RaEndpointReprotoxConcert(this.DBConn);
                    break;
                default:
                    raEndpoint = null;
            }
        } catch (InitFailureException exception) {
            log.error("Could not initialize endpoint {} - {}", endpoint, exception.getMessage());
            throw new RaException("Unable to initialize endpoint " + endpoint);
        }
        return raEndpoint;
    }

    private RaResponse parseResults(RaResultsContainer res) {
        String endpoint = res.getEndpoint().getName();

        iRaObjectRootMolecule root = (iRaObjectRootMolecule) res.getRootNode();
        String prediction = root.getPrediction();
        double mw = 0;
        double logP = 0;
        boolean logPExperimental = false;
        for (RaAdditionalValue val: root.getAdditionalValues()) {
            if (val.getName().equals("Molecular Weight"))
                mw = val.getNumericalValue();
            else if (val.getName().contains("LogP")) {
                logP = val.getNumericalValue();
                if (val.getName().contains("experimental"))
                    logPExperimental = true;
            }
        }
        MoleculeDTO rootMolecule = new MoleculeDTO(
                root.getMolecule().GetCAS(),
                root.getMolecule().GetSMILES(),
                retrieveDepiction(root.getMolecule().GetSMILES()),
                mw,
                logP,
                logPExperimental
        );

        List<SimilarMoleculeDTO> similarMolecules = new ArrayList<>();
        List<RuleDTO> rules = new ArrayList<>();
        List<InterpolationDTO> interpolationNodes = new ArrayList<>();

        /*
        java.util.Deque<Iterator<iRaObject>> childrenToParse = new java.util.ArrayDeque<>();
        childrenToParse.add(root.getChildren());
        while (!childrenToParse.isEmpty()) {
            Iterator<iRaObject> children = childrenToParse.poll();
            while (children.hasNext()) {
                iRaObject node = children.next();
                switch (node.getObjectType()) {
                    case RaObject.RA_OBJECT_SIM_MOLECULE:
                        iRaObjectSimMolecule sim = (iRaObjectSimMolecule) node;
                        SimilarMoleculeDTO similarMoleculeDTO = new SimilarMoleculeDTO(
                                sim.getMolecule().CAS,
                                sim.getMolecule().CommonName,
                                sim.getSimilarity(),
                                sim.getFormattedExperimentalValue()
                        );
                        similarMolecules.add(similarMoleculeDTO);
                        childrenToParse.add(node.getChildren());
                        break;
                    case RaObject.RA_OBJECT_RULE:
                        iRaObjectRule rule = (iRaObjectRule) node;
                        RuleDTO ruleDTO = new RuleDTO(
                                rule.getAlert().getName(),
                                rule.getAlert().getDescription(),
                                rule.getAlert().getSMARTS()
                        );
                        rules.add(ruleDTO);
                        childrenToParse.add(node.getChildren());
                        break;
                    case RaObject.RA_OBJECT_CONNECTOR:
                        childrenToParse.add(node.getChildren());
                        break;
                    case RaObject.RA_OBJECT_INTERPOLATION:
                    default:
                }
            }
        }
        */

        Iterator<iRaObject> children = root.getChildren();
        while (children.hasNext()) {
            iRaObject node = children.next();
            switch (node.getObjectType()) {
                case RaObject.RA_OBJECT_SIM_MOLECULE:
                    iRaObjectSimMolecule sim = (iRaObjectSimMolecule) node;
                    /*double*/ mw = 0;
                    /*double*/ logP = 0;
                    /*boolean*/ logPExperimental = false;
                    for (RaAdditionalValue val: sim.getAdditionalValues()) {
                        if (val.getName().equals("Molecular Weight"))
                            mw = val.getNumericalValue();
                        else if (val.getName().contains("LogP")) {
                            logP = val.getNumericalValue();
                            if (val.getName().contains("experimental"))
                                logPExperimental = true;
                        }
                    }
                    SimilarMoleculeDTO similarMoleculeDTO = new SimilarMoleculeDTO(
                            new MoleculeDTO(
                                    sim.getMolecule().CAS,
                                    retrieveSmiles(res, sim),
                                    retrieveDepiction(retrieveSmiles(res, sim)),
                                    mw,
                                    logP,
                                    logPExperimental
                            ),
                            sim.getSimilarity(),
                            sim.getFormattedExperimentalValue()
                    );
                    similarMolecules.add(similarMoleculeDTO);
                    System.out.println("sim mol " + sim.getMolecule().CAS);
                    break;
                case RaObject.RA_OBJECT_RULE:
                    iRaObjectRule rule = (iRaObjectRule) node;
                    DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
                    InternationalSymbols.setDecimalSeparator('.');
                    java.text.DecimalFormat df3 = new java.text.DecimalFormat("0.###", InternationalSymbols);
                    java.text.DecimalFormat df5 = new java.text.DecimalFormat("0.#####", InternationalSymbols);
                    String experimentalAccuracy;
                    try {
                        experimentalAccuracy = df3.format(rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY));
                    } catch (PropertyNotFoundException e) { experimentalAccuracy = ""; }
                    String experimentalAccuracyBiocide;
                    try {
                        experimentalAccuracyBiocide = df3.format(rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY_BIOCIDES));
                    } catch (PropertyNotFoundException e) { experimentalAccuracyBiocide = ""; }
                    String fisher;
                    try {
                        double fisherValue = rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_FISHER);
                        if (fisherValue < 0.00001)
                            fisher = "< 10e-6";
                        else fisher = df5.format(fisherValue);
                    } catch (PropertyNotFoundException e) { fisher = ""; }
                    RuleDTO ruleDTO = new RuleDTO(
                            rule.getAlert().getName(),
                            rule.getAlert().getDescription(),
                            rule.getAlert().getSMARTS(),
                            experimentalAccuracy,
                            experimentalAccuracyBiocide,
                            fisher,
                            rule.getShape(),
                            rule.getSizeFactor()
                    );
                    rules.add(ruleDTO);
                    System.out.println("rule" + rule.getAlert().getName());
                    break;
                case RaObject.RA_OBJECT_CONNECTOR:
                    iRaObjectConnector con = (iRaObjectConnector) node;
                    System.out.println("connector " + con.getChildrenSize());
                    break;
                case RaObject.RA_OBJECT_INTERPOLATION:
                    iRaObjectInterpolation inter = (iRaObjectInterpolation) node;
                    ArrayList<InterpolationPoint> points = new ArrayList<>();
                    int numPoints = inter.getDataset().Count();
                    for (int i=0; i<numPoints; i++) {
                        InterpolationDataPoint p = inter.getDataset().GetPoint(i);
                        InterpolationPoint point = new InterpolationPoint(
                                p.GetMark(),
                                p.GetCoordinates().X,
                                p.GetCoordinates().Y
                        );
                        points.add(point);
                    }
                    InterpolationDTO interpolationDTO = new InterpolationDTO(
                            inter.getAxisXTitle(),
                            inter.getAxisYTitle(),
                            inter.getDataset().GetTargetLine().X,
                            inter.getDataset().GetTargetLine().Y,
                            points
                    );
                    interpolationNodes.add(interpolationDTO);
                    System.out.println("interpolation " + inter.getAxisYTitle());
                    break;
                default:
            }
        }

        return new RaResponse(endpoint, prediction, rootMolecule, similarMolecules,
                rules, interpolationNodes);
    }

    private String retrieveSmiles(RaResultsContainer res, iRaObjectSimMolecule sim) {
        String smiles = "";
        try {
            smiles = res.getDBConn().GetStructure(sim.getMolecule().Id).SMILES;
        } catch (Exception e) {}
        return smiles;
    }

    private String retrieveDepiction(String smiles) {
        if (smiles == null || smiles.isEmpty()) return "";
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        try {
            sdg.setMolecule(SmilesMolecule.Convert(smiles).GetStructure());
        } catch (InvalidMoleculeException e) {}
        try { sdg.generateCoordinates(); } catch (CDKException e) { }
        IAtomContainer mol = sdg.getMolecule();
        BufferedImage image;
        try {
            image = Depiction.DepictMolecule(mol, imgWidth, imgHeight);
        } catch (CDKException | GenericFailureException e) {
            return new String();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) { }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
