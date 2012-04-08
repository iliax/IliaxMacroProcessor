
package iliaxmacroprocessor;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.google.common.base.Preconditions.*;

/**
 *
 * @author iliax
 */
public class Macros {

    public static final Macros ROOT_MACROS = new Macros("root", MacrosContext.ROOT_MACROS_CONTEXT) {

        @Override
        public List<Macros> getNestedMacroses() {
            return FakeList.FAKE_LIST;
        }
    };
    /** unique! */
    private String _name;
    private List<String> _strings = new ArrayList<String>();
    private List<Macros> _nestedMacroses = new ArrayList<Macros>();
    private MacrosContext _context;
    /** int- относительный номер строки от начала макроса */
    private Map<String, Integer> _labels = new HashMap<String, Integer>();

    public Macros(String _name, MacrosContext macrosContext) {
        this._name = checkNotNull(_name);
        this._context = checkNotNull(macrosContext);
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public void setNestedMacroses(List<Macros> _nestedMacroses) {
        this._nestedMacroses = _nestedMacroses;
    }

    public void setStrings(List<String> _strings) {
        this._strings = _strings;
    }

    public String getName() {
        return _name;
    }

    public List<Macros> getNestedMacroses() {
        return _nestedMacroses;
    }

    public List<String> getStrings() {
        return _strings;
    }

    public MacrosContext getContext() {
        return _context;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Macros)) {
            return false;
        }

        Macros m = (Macros) obj;
        return m._name.equals(_name);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this._name != null ? this._name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String str = "MACROS " + _name + " []";

        str += "\n";
        for (String s : _strings) {
            str += " " + s + "\n";
        }
        str += "MEND" + " nested: ";
        if (_nestedMacroses.isEmpty()) {
            str += "N0NE";
        } else {
            for (Macros m : _nestedMacroses) {
                str += m.getName() + "[] ";
            }
        }

        return str;
    }

    private static class FakeList extends ArrayList {

        static final FakeList FAKE_LIST = new FakeList();

        @Override
        public boolean add(Object e) {
            return true;
        }
    }
}
