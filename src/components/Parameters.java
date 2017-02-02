package components;

import java.util.HashMap;
import java.util.Map;

//NO LONGER IN USE FOR THE LATER CASES OF THE MODEL!!!!!!

public class Parameters extends HashMap<String, Double> {

    public void initialise() {
        globalParameters = new HashMap<>(1000);
        globalParameters.put("lambda_T",0.03);
        globalParameters.put("lambda_min", 0.03);
        globalParameters.put("kappa_T", 1.02);
        globalParameters.put("kappa_min", 1.0);
        globalParameters.put("alpha", 0.001);
        globalParameters.put("beta", 0.8);
        globalParameters.put("InitialStockPrice", 2.0);
        globalParameters.put("NewStockPrice", 1.5);



        //TODO: List all the parameters!

    }

    public Map<String, Double> getGlobalParameters() {
        return globalParameters;
    }

    private HashMap<String, Double> globalParameters;
}
