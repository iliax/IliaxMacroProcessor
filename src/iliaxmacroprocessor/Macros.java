
package iliaxmacroprocessor;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.google.common.base.Preconditions.*;

/**
 *
 * @author iliax
 */
public class Macros {

    public static final Macros ROOT_MACROS = new Macros("", null) {

        @Override
        public List<Macros> getNestedMacroses() {
            return super.getNestedMacroses();
        }

        @Override
        public Macros getParentMacros() {
            return Macros.ROOT_MACROS;
        }

    };
    
    /** unique! */
    private String _name;

    private List<String> _strings = new ArrayList<String>();
    
    private List<Macros> _nestedMacroses = new ArrayList<Macros>();

    private Macros _parentMacros;

    private VariablesStore _variables = new VariablesStore();

    /** int- относительный номер строки от начала макроса */
    private Map<String, Integer> _labels = new HashMap<String, Integer>();

    public Macros(String _name, Macros parent) {
        this._name = checkNotNull(_name);
        this._parentMacros = parent;
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

    public VariablesStore getVariables() {
        return _variables;
    }

    public Macros getParentMacros() {
        return _parentMacros;
    }

    public boolean addLabel(String lbl, Integer strNum){
        if(_labels.containsKey(lbl)){
            return false;
        }
        
        _labels.put(lbl, strNum);
        return true;
    }

    public boolean isLabelExist(String lbl){
        return _labels.containsKey(lbl);
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
        String str = "MACROS " + _name + " [...]";

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

        str += "\n" + getVariables().toString();

        str += "\n" + _labels.toString();

        return str;
    }

    private static class FakeList extends ArrayList {

        static final FakeList FAKE_LIST = new FakeList();

        @Override
        public boolean add(Object e) {
            return true;
        }
    }

   public class VariablesStore {

        private Map<String, Variable> _variables = new LinkedHashMap<String, Variable>();

        public VariablesStore() {
        }

        private List<String> varsSequence = new ArrayList<String>();

        public int varSeqCount(){
            return varsSequence.size();
        }

        public List<String> getVarsSequence() {
            return varsSequence;
        }


        public boolean addVariable(String varName){
            if(_variables.containsKey(varName)){
                return false;
            } else {
                varsSequence.add(varName);
                _variables.put(varName, new Variable(varName));
                return true;
            }
        }

        public String getVariableVAlFromGlobalContext(String varName){
            if(_variables.get(varName) != null && /*check it->*/ _variables.get(varName).getValue()!=null){ //TODO test it
                return _variables.get(varName).getValue();
            } else {
                if(!Macros.this.getParentMacros().equals(ROOT_MACROS)){
                    return Macros.this.getParentMacros().getVariables().getVariableVAlFromGlobalContext(varName);
                } else {
                    return null;
                }
            }
        }

        public void clearVarsStore(){
            _variables = new HashMap<String, Variable>();
        }

        public boolean addKeyVariable(String varName, String defVal){
            if(_variables.containsKey(varName)){
                return false;
            } else {
                _variables.put(varName, new Variable(varName, defVal));
                return true;
            }
        }

        public String getVariableValue(String varName){
            return _variables.get(varName).getValue();
        }

        public boolean isVariableExists(String var){
            return _variables.containsKey(var);
        }

        public boolean isVariableKeyVar(String var){
            return _variables.get(var).isKeyVariable();
        }

        public boolean setVariableValue(String varName, String value){
           if(!_variables.containsKey(varName)){
                return false;
            } else {
                _variables.get(varName).setValue(value);
                return true;
            }
        }

        public boolean setVariableValue(int varNum, String val){
            if(varNum > varsSequence.size()){
                return false;
            }

            _variables.get(varsSequence.get(varNum)).setValue(val);
            return true;
        }

        public class Variable {

            private String name, value;
            private boolean keyVariable = false;

            public Variable(String name, String value) {
                this.name = name;
                this.value = value;
                this.keyVariable = true;
            }

            public Variable(String name) {
                this.name = name;
                value = null;
            }

            public boolean isKeyVariable() {
                return keyVariable;
            }

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return name + " " + value + " " + keyVariable;
            }
        }

        @Override
        public String toString() {
            return _variables.toString();
        }


    }
    
}
