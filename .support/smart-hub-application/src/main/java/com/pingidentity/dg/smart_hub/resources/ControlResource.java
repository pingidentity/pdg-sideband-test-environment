package com.pingidentity.dg.smart_hub.resources;

import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.PATCH;
import javax.ws.rs.PathParam;

import com.pingidentity.dg.smart_hub.api.Control;
import com.pingidentity.dg.smart_hub.api.validation.PartialUpdate;
import com.pingidentity.dg.smart_hub.db.BaseDao;

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
  public Optional<Control> post(String id, Control changedEntity) {

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
  public Optional<Control> patch(@PathParam("id") @NotBlank String id,
      @NotNull @Valid @ConvertGroup(to = PartialUpdate.class) Control changedEntity) {

    Optional<Control> foundControl = controlDao.findById(id);
    if (foundControl.isPresent()) {

      Control oldControl = foundControl.get();

      // Only writeable controls may be updated
      if (oldControl.getReadOnly()) {
        throw new UnsupportedOperationException(READ_ONLY_CONTROL);
      }

      // Only make a change if the calls remaining is non-zero
      Integer callsRemaining = oldControl.getCallsRemaining();
      if (callsRemaining != 0) {
        // Only certain properties can be changed
        changedEntity.setName(oldControl.getName());
        changedEntity.setReadOnly(oldControl.getReadOnly());
        changedEntity.setCallsRemaining(
            callsRemaining < 1 ? callsRemaining : callsRemaining - 1);
        return controlDao.update(id, changedEntity);
      }
    }
    return foundControl;
  }

}
