package ua.orlov.betreactive.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseEntity {

    @Id
    private UUID id;

}
