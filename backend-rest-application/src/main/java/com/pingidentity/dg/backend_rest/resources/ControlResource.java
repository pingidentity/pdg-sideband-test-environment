package com.pingidentity.dg.backend_rest.resources;

import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;

import com.pingidentity.dg.backend_rest.api.Control;
import com.pingidentity.dg.backend_rest.api.validation.Update;
import com.pingidentity.dg.backend_rest.db.BaseDao;

import io.dropwizard.jersey.PATCH;

/**
 * A {@link Control} resource.
 *
 */
@PermitAll
public class ControlResource extends BaseInstanceResource<Control> {

  private static final String POST_NOT_SUPPORTED = "POST_NOT_SUPPORTED";
  private static final String READ_ONLY_CONTROL = "READ_ONLY_CONTROL";

  @Override
  protected BaseDao<Control> getEntityDao() {
    return controlDao;
  }

  @Override
  public Optional<Control> post(@NotBlank String id,
      @NotNull @Valid @ConvertGroup(to = Update.class) Control changedEntity) {

    // Use PATCH for controls to differentiate them
    throw new UnsupportedOperationException(POST_NOT_SUPPORTED);
  }

  /**
   * Updates a control.
   *
   * @param id            The control id.
   * @param changedEntity The changed control.
   * @return The changed control, if it exists.
   */
  @PATCH
  public Optional<Control> patch(@NotBlank String id,
      @NotNull @Valid @ConvertGroup(to = Update.class) Control changedEntity) {

    Optional<Control> foundControl = controlDao.findById(id);
    if (foundControl.isPresent()) {

      // Only writeable controls may be updated
      if (foundControl.get().getReadOnly()) {
        throw new UnsupportedOperationException(READ_ONLY_CONTROL);
      }
      return controlDao.update(id, changedEntity);
    }
    return foundControl;
  }

}
