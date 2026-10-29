package insilico.readacross.service;

import insilico.core.exception.InitFailureException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.RaEngine;
import insilico.readacross.calculation.endpoints.*;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.local.DBConnectorLocal;
import insilico.readacross.entity.*;
import insilico.readacross.exception.InvalidSmilesException;
import insilico.readacross.exception.RaException;
import insilico.readacross.type.interfaces.iRaObject;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaObject;
import insilico.readacross.type.objects.RaResultsContainer;
import insilico.readacross.utils.PrepareDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class ToxreadService {

    @Value("${toxread.db.load_from_serialized_file}")
    private boolean LOAD_FROM_SERIALIZED_FILE;
    @Value("${toxread.db.db_file}")
    private String DB_FILE;

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
        RootMoleculeDTO rootMoleculeDTO = new RootMoleculeDTO(
                root.getMolecule().GetSMILES(),
                root.getMolecule().GetCAS(),
                root.getPrediction()
        );

        List<SimilarMoleculeDTO> similarMolecules = new ArrayList<>();
        List<RuleDTO> rules = new ArrayList<>();

        Iterator<iRaObject> children = root.getChildren();
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
                    break;
                case RaObject.RA_OBJECT_RULE:
                    iRaObjectRule rule = (iRaObjectRule) node;
                    RuleDTO ruleDTO = new RuleDTO(
                            rule.getAlert().getName(),
                            rule.getAlert().getDescription(),
                            rule.getAlert().getSMARTS()
                    );
                    rules.add(ruleDTO);
                    break;
                case RaObject.RA_OBJECT_CONNECTOR:
                case RaObject.RA_OBJECT_INTERPOLATION:
                default:
            }
        }

        return new RaResponse(endpoint, rootMoleculeDTO, similarMolecules, rules);
    }

}
