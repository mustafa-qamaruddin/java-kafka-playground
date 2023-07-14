package classification;

import java.util.List;
import java.util.Random;

public class ClassificationRepository {

  private final List<ClassificationDecision> classificationData;

  public ClassificationRepository() {
    this.classificationData = List.of(
        new ClassificationDecision(
            "http://aexpresswaythreat.co.uk/564x/3c/cf/1a/3ccf1a5b17457d6.jpg",
            3,
            "2018-06-08T17:10:50Z",
            1
        ),
        new ClassificationDecision(
            "http://tetarhn.trade",
            1285,
            "2018-06-08T17:10:51Z",
            23
        ),
        new ClassificationDecision(
            "http://blogs.tripple-rock.com/simonreindl/ct.ashx",
            3,
            "2018-06-08T17:10:55Z",
            42
        )
    );
  }

  public ClassificationDecision findNext() {
    Random random = new Random();
    int randomIndex = random.nextInt(classificationData.size());
    return classificationData.get(randomIndex);
  }
}
