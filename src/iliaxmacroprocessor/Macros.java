/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iliaxmacroprocessor;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import static com.google.common.base.Preconditions.*;

/**
 *
 * @author iliax
 */
public class Macros {

    public static final Macros ROOT_MACROS = new Macros("root", MacrosContext.ROOT_MACROS_CONTEXT);

    /** unique! */
    private String _name;

    private List<String> _strings = new ArrayList<String>();

    private List<Macros> _nestedMacroses = new ArrayList<Macros>();

    private MacrosContext _context;

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
        if(! (obj instanceof Macros)){
            return false;
        }

        Macros m = (Macros)obj;
        return m._name.equals(m._name);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this._name != null ? this._name.hashCode() : 0);
        return hash;
    }



}
