package mundo;

public class Carta {

    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Palo de la carta.
     */
    private String palo;

    /**
     * Representación de la carta.
     */
    private String representacion;

    /**
     * Nombre de la imagen de la carta.
     */
    private String imagen;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Constructor de la clase.
     * @param pPalo palo de la carta.  pPalo.equals(PICAS) ||  pPalo.equals(TREBOLES) ||  pPalo.equals(CORAZONES) || pPalo.equals(DIAMANTES)
     * @param pRepresentacion representación de la carta. AS,2,3,4,5,6,7,8,9,10,J,Q,K
     * @param pImagen Imagen de la carta
     */
    public Carta( String pPalo, String pRepresentacion, String pImagen )
    {
        palo = pPalo;
        representacion = pRepresentacion;
        imagen = pImagen;
    }

    // -----------------------------------------------------------------
    // Métodos
    // -----------------------------------------------------------------

    /**
     * Devuelve el palo de la carta.
     * @return Palo de la carta.
     */
    public String darPalo( )
    {
        return palo;
    }

    /**
     * Devuelve la representación de la carta.
     * @return Representación de la carta.
     */
    public String darRepresentacion( )
    {
        return representacion;
    }

    /**
     * Devuelve la imagen de la carta.
     * @return Imagen de la carta.
     */
    public String darImagen( )
    {
        return imagen;
    }

    /**
     * Devuelve la información de la carta en una cadena con el formato solicitado por el servidor.
     * @return Información de la carta.
     */
    public String toString( )
    {
        return "CARTA:" + palo + ":" + representacion + ":" + imagen;
    }

    /**
     * Compara dos cartas por su representación.
     * @param pCarta Carta con la cual se va a comparar.
     * @return Devuelve 0 si las cartas tienen la misma numeración. <br>
     *         Devuelve -1 si la carta dada por parámetro tiene una valor "MAYOR" para la numeración. <br>
     *         Devuelve 1 si la carta dada por parámetro tiene una valor "MENOR" para la numeración. <br>
     */
    public int comparar( Carta pCarta )
    {

        if( representacion.equals( pCarta.darRepresentacion( ) ) )
            return 0;
        else if( darNumeroRepresentacion( representacion ) < darNumeroRepresentacion( pCarta.darRepresentacion( ) ) )
            return -1;
        else
            return 1;
    }

    /**
     * Devuelve el numero de la representación de la carta.
     * @param pRepresentacion representación de la carta.
     * @return numero de la representación de la carta.
     */
    private int darNumeroRepresentacion( String pRepresentacion )
    {

        if( pRepresentacion.equals( "AS" ) )
            return 1;
        else if( pRepresentacion.equals( "J" ) )
            return 11;
        else if( pRepresentacion.equals( "Q" ) )
            return 12;
        else if( pRepresentacion.equals( "K" ) )
            return 13;
        else
            return Integer.valueOf( pRepresentacion ).intValue( );
    }



}
