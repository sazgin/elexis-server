package info.elexis.server.core.connector.elexis.jpa.model.annotated;

import info.elexis.server.core.connector.elexis.jpa.model.annotated.Kontakt;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.0.v20160725-rNA")
@StaticMetamodel(KontaktAdressJoint.class)
public class KontaktAdressJoint_ { 

    public static volatile SingularAttribute<KontaktAdressJoint, Integer> otherRType;
    public static volatile SingularAttribute<KontaktAdressJoint, Kontakt> otherKontakt;
    public static volatile SingularAttribute<KontaktAdressJoint, Kontakt> myKontakt;
    public static volatile SingularAttribute<KontaktAdressJoint, String> bezug;
    public static volatile SingularAttribute<KontaktAdressJoint, Integer> myRType;

}