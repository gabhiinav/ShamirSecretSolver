import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class ShamirSecretSolver {

    // second: 271644355

    // Function to decode a value based on its base
    private static BigInteger decodeValue(int base, String value) {
        return new BigInteger(value, base);
    }

    // Function to perform Lagrange interpolation using BigInteger
    private static BigInteger lagrangeInterpolation(List<BigInteger[]> points) {
        BigInteger constantTerm = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger x_i = points.get(i)[0];
            BigInteger y_i = points.get(i)[1];
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger x_j = points.get(j)[0];
                    numerator = numerator.multiply(x_j.negate());
                    denominator = denominator.multiply(x_i.subtract(x_j));
                }
            }

            // Compute term and add to constant term
            BigInteger term = y_i.multiply(numerator).divide(denominator);
            constantTerm = constantTerm.add(term);
        }

        return constantTerm;
    }

    // Main function to parse JSON input and find the secret
    private static BigInteger findSecret(String filePath) throws Exception {
        // Read JSON file
        String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject data = new JSONObject(jsonData);

        // Extract keys and roots
        JSONObject keys = data.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<BigInteger[]> points = new ArrayList<>();

        for (String key : data.keySet()) {
            if (!key.equals("keys")) {
                JSONObject pointData = data.getJSONObject(key);
                int x = Integer.parseInt(key);
                int base = pointData.getInt("base");
                String value = pointData.getString("value");
                points.add(new BigInteger[]{BigInteger.valueOf(x), decodeValue(base, value)});
            }
        }

        // Use the first k roots for interpolation
        List<BigInteger[]> selectedPoints = points.subList(0, k);

        // Find the secret (constant term)
        return lagrangeInterpolation(selectedPoints);
    }

    public static void main(String[] args) {
        try {
            // Paths to the test case files
            String testCase1Path = "./first.json";
            String testCase2Path = "./second.json";

            // Solve for each test case
            BigInteger secret1 = findSecret(testCase1Path);
            BigInteger secret2 = findSecret(testCase2Path);

            // Output the secrets
            System.out.println("Secret for Test Case 1: " + secret1);
            System.out.println("Secret for Test Case 2: " + secret2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}