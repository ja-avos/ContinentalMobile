package mundo;

public class ContinentalException extends Exception {

    // -----------------------------------------------------------------
    // Constantes
    // -----------------------------------------------------------------

    /**
     * Constante para la serialización.
     */
    private static final long serialVersionUID = 113229143589381951L;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Construye una nueva excepción de este tipo con el mensaje indicado.
     * @param pMensaje El mensaje que describe la causa de la excepción. mensaje != null.
     */
    public ContinentalException( String pMensaje )
    {
        super( pMensaje );
        System.out.println(pMensaje);
    }

}
