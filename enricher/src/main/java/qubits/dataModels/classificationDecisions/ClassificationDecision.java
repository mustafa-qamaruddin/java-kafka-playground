package qubits.dataModels.classificationDecisions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ClassificationDecision {
  private String url;
  private int classification;
  private String created;
  private int logic;
}
