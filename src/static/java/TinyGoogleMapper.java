/**
* Mapper program for use with TinyGoogle for cs1699
* @author Jake Halloran and Andrew Levandoski
**/
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TinyGoogleMapper
  extends Mapper<LongWritable, Text, Text, IntWritable> {

  private static final int MISSING = 9999;
  public static count = 0; //count number of words processed
  @Override
  public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    
    private Text Word = new Text();
    String fileName = ((FileSplit) context.getInputSplit()).getPath().getName(); //get file name to append to key
    
    StringTokenizer itr = new StringTokenizer(value.toString());
    while(itr.hasMoreTokens()){
      word.set(itr.nextToken());
      Text output = new Text(new String(fileName + " " + word.toString()));
      contex,write(output,count++); //write the fule name + the word + the word number
    }
    
  }
}