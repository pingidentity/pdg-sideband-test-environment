package com.pingidentity.dg.smart_hub.auth;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import com.pingidentity.dg.smart_hub.api.Person;

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
public class SmartHubAuthenticator
    implements Authenticator<SmartHubCredentials, Principal> {

  private final Map<String, Person> authCredentials;

  @Override
  public Optional<Principal> authenticate(SmartHubCredentials credentials)
      throws AuthenticationException {
    String personId = credentials.getPersonId();

    if (authCredentials.containsKey(personId)) {
      return Optional.of(new PrincipalImpl(personId));
    }
    return Optional.empty();
  }

}
