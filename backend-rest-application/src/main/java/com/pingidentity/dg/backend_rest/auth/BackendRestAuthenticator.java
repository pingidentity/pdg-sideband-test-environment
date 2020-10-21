package com.pingidentity.dg.backend_rest.auth;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import com.pingidentity.dg.backend_rest.api.Person;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PrincipalImpl;
import lombok.RequiredArgsConstructor;

/**
 * Authenticates a user by checking that their person ID exists in an in-memory
 * map.
 *
 */
@RequiredArgsConstructor
public class BackendRestAuthenticator
    implements Authenticator<BackendRestCredentials, Principal> {

  private final Map<String, Person> authCredentials;

  @Override
  public Optional<Principal> authenticate(BackendRestCredentials credentials)
      throws AuthenticationException {
    String personId = credentials.getPersonId();

    if (authCredentials.containsKey(personId)) {
      return Optional.of(new PrincipalImpl(personId));
    }
    return Optional.empty();
  }

}
