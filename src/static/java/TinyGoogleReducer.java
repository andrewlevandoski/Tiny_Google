/**
* Reducer for TinyGoogle project in cs 1699
* @author Jake Halloran and Andrew Levandoski
**/
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TinyGoogleReducer
  extends Reducer<Text, IntWritable, Text, IntWritable> {
  
  @Override
  public void reduce(Text key, Iterable<IntWritable> values,
      Context context)
      throws IOException, InterruptedException {
    
    int maxValue = Integer.MIN_VALUE;
    int count = 0;
    for (IntWritable value : values) {
      minValue = Math.min(minValue, value.get());
      count++;
    }
    key = new Text(new String(key.toString() + " " +minValue.toString()));
    context.write(key, new IntWritable(count));
  }
}