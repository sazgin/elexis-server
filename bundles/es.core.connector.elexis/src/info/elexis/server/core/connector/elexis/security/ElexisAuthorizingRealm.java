package info.elexis.server.core.connector.elexis.security;

import java.util.Optional;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.osgi.service.component.annotations.Component;

import info.elexis.server.core.connector.elexis.internal.ElexisEntityManager;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Role;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.User;

/**
 * DISABLED
 * @author marco
 *
 */
@Component(service = Realm.class, enabled = false)
public class ElexisAuthorizingRealm extends AuthorizingRealm {

	public static final String REALM_NAME = "elexis-db-realm";

	public ElexisAuthorizingRealm() {
		setName(REALM_NAME);
		setCredentialsMatcher(new ElexisCredentialsMatcher());
	}

	@Override
	public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String userId = (String) principals.fromRealm(getName()).iterator().next();
		Optional<User> oUser = getUserById(userId);
		if (oUser.isPresent()) {
			User user = oUser.get();
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			if (user.isAdministrator()) {
				// TODO
				info.addRole("admin");
			}
			for (Role role : user.getRoles()) {
				info.addRole(role.getId());
			}
			return info;
		} else {
			return null;
		}
	}

	@Override
	public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String userid = upToken.getUsername();

		Optional<User> oUser = getUserById(userid);
		if (oUser.isPresent()) {
			User user = oUser.get();
			if (user.isActive() && !user.isDeleted()) {
				return new SimpleAuthenticationInfo(userid, user, REALM_NAME);
			}
		}

		return null;
	}

	private Optional<User> getUserById(String userid) {
		return Optional.ofNullable(ElexisEntityManager.createEntityManager().find(User.class, userid));
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		if (ElexisEntityManager.createEntityManager() != null && ElexisEntityManager.createEntityManager().isOpen()) {
			// we can only support authentication if we are connected
			return super.supports(token);
		}
		return false;
	}
}
