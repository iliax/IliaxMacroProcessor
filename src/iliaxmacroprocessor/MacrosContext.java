package iliaxmacroprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author iliax
 */
public class MacrosContext {

    public static final MacrosContext ROOT_MACROS_CONTEXT = new MacrosContext(null){

        @Override
        public Macros getMacros() {
            return Macros.ROOT_MACROS;
        }

    };

    private Macros _parentMacros;

    private VariablesStore _variables = new VariablesStore();

    public MacrosContext(Macros parentMacros) {
        this._parentMacros = parentMacros;
    }

    public Macros getMacros() {
        return _parentMacros;
    }

    public VariablesStore getVariablesStore() {
        return _variables;
    }

    public void clearVarsStore(){
            _variables.clearVarsStore();
        }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof MacrosContext)){
            return false;
        }

        MacrosContext that = (MacrosContext) obj;
        return getMacros().equals(that.getMacros());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this._parentMacros != null ? this._parentMacros.hashCode() : 0);
        return hash;
    }

    public class VariablesStore {

        private Map<String, Variable> _variables = new LinkedHashMap<String, Variable>();

        public VariablesStore() {
        }

        private List<String> varsSequence = new ArrayList<String>();

        public boolean addVariable(String varName){
            if(_variables.containsKey(varName)){
                return false;
            } else {
                varsSequence.add(varName);
                _variables.put(varName, new Variable(varName));
                return true;
            }
        }

        public void clearVarsStore(){
            _variables = new HashMap<String, Variable>();
        }

        public boolean addKeyVariable(String varName, String defVal){
            if(!_variables.containsKey(varName)){
                return false;
            } else {
                _variables.put(varName, new Variable(varName, null, defVal));
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
            return _variables.get(var).getDefaultValue() != null;
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
            
            private String name, value, defaultValue;

            public Variable(String name, String value, String defaultValue) {
                this.name = name;
                this.value = value;
                this.defaultValue = defaultValue;
            }

            public Variable(String name) {
                this.name = name;
                value = null;
                defaultValue = null;
            }

            public String getDefaultValue() {
                return defaultValue;
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
                return name + " " + value + " " + defaultValue;
            }
        }

        @Override
        public String toString() {
            return _variables.toString();
        }


    }
    
    
}
