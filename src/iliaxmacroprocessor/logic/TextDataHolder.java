package iliaxmacroprocessor.logic;

import org.apache.log4j.Logger;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static com.google.common.base.Preconditions.*;

/**
 *
 * @author iliax
 */
public class TextDataHolder {

    private static final Logger LOG = Logger.getLogger(TextDataHolder.class.getName());
    
    private static final String LS = System.getProperty("line.separator");

    public static final Predicate NO_EMPTY_STRINGS = new Predicate<String>() {

        @Override
        public boolean apply(String t) {
            return !(t == null || t.trim().isEmpty());
        }
    };

    public static final Predicate NO_FILTER = new Predicate<String>() {

        @Override
        public boolean apply(String t) {
            return true;
        }
    };
    
    public final File file;

    public TextDataHolder(File file) {
        this.file = checkNotNull(file);

        if (!file.exists()) {
            throw new RuntimeException("file does not exist!");
        }

    }

    public List<String> getStrings(Predicate howToFilter){
        List<String> strings;

        try {
            strings = Files.readLines(file, Charsets.UTF_8);
        } catch (IOException ex) {
            LOG.warn("can't read this file: " + file.getAbsolutePath(), ex);
            throw new RuntimeException("can't read this file " + file.getAbsolutePath(), ex);
        }

        strings = Lists.newLinkedList(Collections2.filter(strings, howToFilter));

        return strings;
    }

    public List<String> getStrings() {
        return getStrings(NO_FILTER);
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();

        List<String> strings = getStrings();
        for (String s : strings) {
            sb.append(s);
            sb.append(LS);
        }

        return sb.toString();
    }

    public void synchTextWithFile(File file, String text) {
        try {
            Files.write(text, file, Charsets.UTF_8);
        } catch (IOException ex) {
            LOG.warn("can't write to file " + file.getAbsolutePath(), ex);
            throw new RuntimeException("can't write to file " + file.getAbsolutePath(), ex);
        }
    }
}
