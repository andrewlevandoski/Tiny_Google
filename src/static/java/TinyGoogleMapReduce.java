/**
* @Author Jake Halloran and Andrew Levandoski
* Map Reduce code for Tiny Google Project of Pitt CS1699
**/ 

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.*

public class TinyGoogleMapReduce {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: TinyGoogleMapReduce <input path> <output path>");
      System.exit(-1);
    }
    
    Job job = new Job();
    job.setJarByClass(MaxTemperature.class);
    job.setJobName("TinyGoogle");

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    job.setMapperClass(TinyGoogleMapper.class);
    job.setReducerClass(TinyGoogleReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}