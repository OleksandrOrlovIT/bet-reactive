package ua.orlov.betreactive.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "users")
public class Event extends BaseEntity {

    @Field(name = "name")
    private String name;

    @Field(name = "start_date")
    private LocalDateTime startDate;

    @Field(name = "end_date")
    private LocalDateTime endDate;

}
