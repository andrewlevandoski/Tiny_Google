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

  //hashmap representing inverted index
  static HashMap <String, ArrayList> invertedIndex = new HashMap <String, ArrayList>();
  
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
      
      //take job output and build index
      if(job.waitForCompletion(true)){
        Path mr_output = new Path(args[1]+"/part-r-00000");
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(mr_output)));
        String line;
        line = br.readLine();
        while(line != null){
          line = br.readLine();
          if(line != null){
            String[] splitted = line.split("\\s+");
            if(splitted.length > 2){
              
              //if the word is already in the inverted index add this books words
              if(invertedIndex.containsKey(splitted[1])){
                ArrayList al = invertedIndex.get(splitted[1]);
                String[] arr = new String[2];
                //book name
                arr[0] = splitted[0];
                //number
                arr[1] = splitted[2].trim();
                al.add(arr);//splitted[0], Integer.parseInt(splitted[2].trim()));
              }
              //if the word is not in the inverted index add a place for it and store this books words
              else{
                ArrayList<String[]> al = new ArrayList<String[]>();
                String[] arr = new String[2];
                arr[0] = splitted[0];
                arr[1] = splitted[2].trim();
                al.add(arr);       //splitted[0], Integer.parseInt(splitted[2].trim()));
                invertedIndex.put(splitted[1], al);
              }
            }
          }
        }
        //close the reader and write the inverted index
        br.close();
        writeIndexToFile();
      }
      
    }
    
  }
  
  public static class TinyGoogleReducer
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
      //key = new Text(new String(key.toString() + " " +Integer.toString(minValue)));
      context.write(key, new IntWritable(count));
    }
  }
  
  public static class TinyGoogleMapper
  extends Mapper<Object, Text, Text, IntWritable> {

    private static final int MISSING = 9999;
    
    @Override
    public void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      
      String fileName = ((FileSplit) context.getInputSplit()).getPath().getName(); //get file name to append to key
      int count = 0; //count number of words processed
      
      StringTokenizer itr = new StringTokenizer(value.toString());
      while(itr.hasMoreTokens()){
        String strippedInput = itr.nextToken().toLowerCase().replaceAll("\\W", "");
        Text output = new Text(new String(fileName + " " + strippedInput));
        context.write(output,new IntWritable(1)); //write the fule name + the word + 1
      }      
    }
  }
  
  //writes the inverted index to hdfs://usr/ubuntu/invertedindex.txt
  public static void writeIndexToFile() throws Exception {
    Path pt=new Path("invertedindex.txt");
    FileSystem fs = FileSystem.get(new Configuration());
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(pt, true)));

    for (Map.Entry<String, ArrayList> entry : invertedIndex.entrySet()) {
      bw.write(entry.getKey()+" ");
      for(int i=0; i<entry.getValue().size(); i++){
        String[] arr = (String[])entry.getValue().get(i);
        bw.write(arr[0]+" "+arr[1]+" ");
      }
      bw.write("\n");
    }
    bw.close();
  }

  public static void SearchMapper extends Mapper<Object, Text, Text, IntWritable> {
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
      //load the inverted index from disk
      Path path = new Path("invertedindex.txt");
      FileSystem fs = FileSystem.get(new Configuration());
      
      //loop over each keyword
      StringTokenizer itr = new StringTokenizer(value.toString());
      while(itr.hasMoreTokens()){
        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));
        String searchTerm = itr.nextToken();
        String line = br.readLine();
        while(line != null){
          String splitted = line.split("\\s+");
          if(splitted[0].equals(searchTerm){
            //write key = doc name, val = numOccurances
            context.write(new Text(splitted[1]),new IntWritable(Integer.parseInt(splitted[2])));
          }
          line = br.readLine();
        }
        //buffered reader doesnt have a rewind so close and open is what has to happen
        br.close();
      }
    }
  }
}