package qubits.dataModels.enrichedClassifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class EnrichedClassification {
  private String url;
  private String domainName;
  private int classification;
  private String created;
  private int logic;
  private Long domainAgeInDays;
}
