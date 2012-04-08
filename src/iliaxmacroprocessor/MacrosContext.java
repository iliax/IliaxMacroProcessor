package iliaxmacroprocessor;

/**
 *
 * @author iliax
 */
public class MacrosContext {

    public static final MacrosContext ROOT_MACROS_CONTEXT = new MacrosContext(null){

        @Override
        public Macros getParentMacros() {
            return Macros.ROOT_MACROS;
        }

    };

    private Macros parentMacros;  

    public MacrosContext(Macros parentMacros) {
        this.parentMacros = parentMacros;
    }

    public Macros getParentMacros() {
        return parentMacros;
    }


    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof MacrosContext)){
            return false;
        }

        MacrosContext that = (MacrosContext) obj;
        return getParentMacros().equals(that.getParentMacros());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.parentMacros != null ? this.parentMacros.hashCode() : 0);
        return hash;
    }

    
    
}
