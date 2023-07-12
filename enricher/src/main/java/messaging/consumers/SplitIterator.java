package messaging.consumers;

import java.util.Iterator;
import java.util.List;

public class SplitIterator<T> implements Iterator<List<T>> {
  private final List<T> list;
  private final int chunkSize;
  private int index;

  public SplitIterator(List<T> list, int chunkSize) {
    this.list = list;
    this.chunkSize = chunkSize;
    this.index = 0;
  }

  @Override
  public boolean hasNext() {
    return index < list.size();
  }

  @Override
  public List<T> next() {
    int fromIndex = index;
    int toIndex = Math.min(index + chunkSize, list.size());
    index = toIndex;
    return list.subList(fromIndex, toIndex);
  }
}
