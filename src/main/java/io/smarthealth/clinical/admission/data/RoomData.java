package io.smarthealth.clinical.admission.data;

import io.smarthealth.clinical.admission.domain.Room;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class RoomData {
    private Long id;
    private String name;
    private Room.Type type;
    private String description;
    private Long wardId;
    private String ward;
    private Boolean active;
}
