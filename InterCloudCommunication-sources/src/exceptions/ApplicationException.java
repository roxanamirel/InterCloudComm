/**********************************************************************************************************
 * Copyright 2012, Distributed Systems Research Laboratory, Technical University of Cluj-Napoca, Romania 
 * http://dsrl.coned.utcluj.ro/
 *  
 * Licensed under the EUPL V.1.1 
 * 
 * European Union Public Licence V. 1.1  
 * You may obtain a copy of this license at
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *  
 **********************************************************************************************************/
package exceptions;

/**
 * <br/>
 * <br/>
 * <p><strong>GAMES Project - Global Control Loop</strong></p>
 * <p><strong>@Author: Technical University of Cluj-Napoca</strong></p>
 * <br/>
 * <a href=http://dsrl.coned.utcluj.ro/ > Distributed Systems research Laboratory </a>
 * <br/>Coordinator: Prof. dr. ing. Ioan Salomie E-mail: Ioan.Salomie@cs.utcluj.ro
 * <br/>
 * <br/>Developers:
 * <br/>Daniel Moldovan(Daniel.Moldovan@cs.utcluj.ro)
 * <br/>Georgiana Copil(Georgiana.Copil@cs.utcluj.ro)
 */

public class ApplicationException extends ServiceCenterAccessException{
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
