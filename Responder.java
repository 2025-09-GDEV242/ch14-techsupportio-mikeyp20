import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Michael Patterson 
 * @version 2025
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {
        responseMap.clear();
        
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get("responses.txt");
        
        try(BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = reader.readLine();
            
            while(line != null) {
                if(line.trim().isEmpty()) {
                    line = reader.readLine();
                    continue;
                }
                String[] keys = line.split(", ");
                StringBuilder response = new StringBuilder(); 
                line = reader.readLine(); 
                
                while(line != null && !line.trim().isEmpty()) {
                    if(response.length() > 0) {
                        response.append(" ");
                    }
                    response.append(line);
                    line = reader.readLine();
                }
                for(String key : keys) {
                    responseMap.put(key.trim(),response.toString());
                }
                if(line!=null) {
                    line = reader.readLine();
                }
            }
        }catch(Exception e ) {
            System.out.println("Error reading responses.txt:" + e.getMessage());
        }
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
      Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
        StringBuilder currentResponse = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            if(line.trim().isEmpty()) {
                if(currentResponse.length() > 0) {
                    defaultResponses.add(currentResponse.toString());
                    currentResponse.setLength(0);
                }
            }
            else {
                if(currentResponse.length() > 0) {
                    currentResponse.append(" ");
                }
                currentResponse.append(line);
            }
            line = reader.readLine();
        }
        if(currentResponse.length() > 0) {
            defaultResponses.add(currentResponse.toString());
        }
        }
        catch(Exception e) {
            System.out.println("Error reading default.txt: " + e.getMessage());
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
}
/**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
