/**
* @Author Jake Halloran and Andrew Levandoski
* Map Reduce code for Tiny Google Project of Pitt CS1699
**/ 


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.util.*;
import java.io.*;

public class TinyGoogleMapReduce {

  public static void main(String[] args) throws Exception {
    if (args.length != 2 && args.length != 3) {
      System.err.println("Usage: TinyGoogleMapReduce <input path> <output path> <option_num>");
      System.exit(-1);
    }
    
    //use mapreduce to build inverted index
    if(args.length == 2 || args[2].equals("1")){
      Configuration conf = new Configuration();
      Job job = Job.getInstance(conf,"TinyGoogleMR");
      job.setJarByClass(TinyGoogleMapReduce.class);
      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));    
      job.setMapperClass(TinyGoogleMapper.class);
      job.setReducerClass(TinyGoogleReducer.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);   
      
      System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
    
  }
  
  public class TinyGoogleReducer
  extends Reducer<Text, IntWritable, Text, IntWritable> {
  
    @Override
    public void reduce(Text key, Iterable<IntWritable> values,
        Context context)
        throws IOException, InterruptedException {
      
      int minValue = Integer.MAX_VALUE;
      int count = 0;
      for (IntWritable value : values) {
        minValue = Math.min(minValue, value.get());
        count++;
      }
      key = new Text(new String(key.toString() + " " +Integer.toString(minValue)));
      context.write(key, new IntWritable(count));
    }
  }
  
  public class TinyGoogleMapper
  extends Mapper<Object, Text, Text, IntWritable> {

    private static final int MISSING = 9999;
    
    @Override
    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      
      Text word = new Text();
      String fileName = ((FileSplit) context.getInputSplit()).getPath().getName(); //get file name to append to key
      int count = 0; //count number of words processed
      
      StringTokenizer itr = new StringTokenizer(value.toString());
      while(itr.hasMoreTokens()){
        word.set(itr.nextToken());
        Text output = new Text(new String(fileName + " " + word.toString()));
        context.write(output,new IntWritable(count++)); //write the fule name + the word + the word number
      }      
    }
  }
}