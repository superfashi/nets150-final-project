import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

final class DataProcessing {
    static final private String url = "https://oracleofbacon.org/data.txt.bz2";
    static final private ObjectMapper mapper = new ObjectMapper();
    static private URL dataSet = null;
    final private List<Util.MovieInfo> movieInfos = new LinkedList<>();

    DataProcessing() {
        if (dataSet == null) {
            try {
                dataSet = new URL(url);
            } catch (MalformedURLException ignored) {
            }
        }
    }

    void acquireData() throws IOException {
        URLConnection urlConnection = dataSet.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        BZip2CompressorInputStream stream = new BZip2CompressorInputStream(inputStream);
        try (Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNextLine()) {
                Util.MovieInfo info = mapper.readValue(scanner.nextLine(), Util.MovieInfo.class);
                movieInfos.add(info);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

    List<String[]> getAllCasts() {
        return movieInfos.stream().map(i -> i.cast).collect(Collectors.toList());
    }
}
