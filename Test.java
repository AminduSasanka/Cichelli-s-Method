import java.util.*;
import java.io.*;

class Test{

  public static void main(String[] args) {
    Methods obj = new Methods();                                  //create an object with methods class

    Scanner keyword_sc = obj.open_file("kywrdsOdd.txt");          //Create a scanner for reading keywords

    long startTime = System.currentTimeMillis();

    obj.read_file(keyword_sc);                                    //reading the file
    System.out.println(obj.keyword_array);                        //printing scanned keywords
    int[] hash_array = new int[obj.keyword_array.size()];         //creating an array to store hash values

    obj.sort_keywords();
    for(int i=0; i<obj.keyword_sort.length; i++){
      System.out.print(obj.keyword_sort[i] + ", ");
    }
    System.out.println();

    obj.create_Htable();
    for(int i=0; i<obj.hTable.length; i++){
      System.out.print(obj.hTable[i] + ", ");
    }
    System.out.println();

    Scanner document_sc = obj.open_file("tstOdd.txt");          //creating scanner to read the document

    obj.read_document(document_sc);
    for(int i=0; i<obj.keyword_count.length; i++){
      System.out.print(obj.keyword_count[i] + ", ");
    }
    System.out.println();

    long endTime = System.currentTimeMillis();
    System.out.println("That took " + (endTime - startTime) + " milliseconds");

    System.out.println("total lines = "+obj.lineCount);
    System.out.println("total words = "+obj.wordCount);
  }
}


class Methods{
  ArrayList<String> keyword_array;      //array list to store read keywords
  String[] keyword_sort;                //to store sorted keywords
  int[] gValue;                         //to store g values
  String[] hTable;                      //store hash table keywords
  int[] keyword_count;                  //store keyword count
  int wordCount = 0;                    //total word count
  int lineCount = 0;                    //total line count

//method for open the file
  public Scanner open_file(String path){
    try {
      Scanner sc = new Scanner(new File(path));
      return sc;
    } catch(Exception e) {
      System.out.println("unable to find the file");
    }
    return null;
  }

//method for read the file
  public void read_file(Scanner sc){
    keyword_array = new ArrayList<String>();          //array list to store scaning keywords
    while(sc.hasNext()){                              //while reach end of the text file
      String a = sc.next();
      keyword_array.add(a);                           //storing scanned keyword in array list
    }
  }

//method to create keywords in decending order as keyword value
  public void sort_keywords(){
    int[] occurance_array = new int[52];                            //to store occurance frequency of letters
    int[] key_values = new int[keyword_array.size()];               //to store keyword values
    Integer[] keyval_sort = new Integer[keyword_array.size()];      //to store sorted values
    keyword_sort = new String[keyword_array.size()];

    //starting to store occurance of letters
    for(int i=0; i<=26; i++){
      occurance_array[i] = 0;                                       //initializing frequency of all letters by 0
    }

    for(int i=0; i<keyword_array.size(); i++){                      //storing occurance of letters
      String word = keyword_array.get(i);
      int fltr = (int)word.charAt(0);
      int lltr = (int)word.charAt(word.length()-1);

      if(fltr>=97 && fltr<=122){                                    //if first letter is a simple letter
        occurance_array[fltr-97] = occurance_array[fltr-97] + 1;
      }else{                                                        //if first letter is a capital letter
        occurance_array[fltr-39] = occurance_array[fltr-39] + 1;
      }
      if(lltr>=97 && lltr<=122){                                    //if last letter is a simple letter
        occurance_array[lltr-97] = occurance_array[lltr-97] + 1;
      }else{                                                        //if last letter is a capital letter
        occurance_array[lltr-39] = occurance_array[lltr-39] + 1;
      }
    }
    //occurance storing is complete and ready

    //starting to store keyword values
    for(int i=0; i<keyword_array.size(); i++){                      //storing keyword values
      String word = keyword_array.get(i);
      int fltr = (int)word.charAt(0);
      int lltr = (int)word.charAt(word.length()-1);

      if(fltr>=97 && fltr<=122){                                    //if first letter is a simple letter
        fltr = occurance_array[fltr-97];
      }else{                                                        //if first letter is a capital letter
        fltr = occurance_array[fltr-39];
      }
      if(lltr>=97 && lltr<=122){                                    //if last letter is a simple letter
        lltr = occurance_array[lltr-97];
      }else{                                                        //if last letter is a capital letter
        lltr = occurance_array[lltr-39];
      }
      int total_v = fltr + lltr;                                    //calculating first and last letter sum
      key_values[i] = total_v;                                      //storing total value in an array
    }
    //keyword values are complete and ready

    //sorting keyword values in decending order
    for(int i=0; i< key_values.length; i++)                         //creating a copy of values array to sort
      keyval_sort[i] = key_values[i];
    Arrays.sort(keyval_sort, Collections.reverseOrder());           //sorting array must be Integer not int. sorting isnt working for int

    //arranging keywords as decending order of keyword values
    for(int i=0; i<keyval_sort.length; i++){                        //sorting keywords as keyvalue order
      for(int j=0; j<key_values.length; j++){
        if(keyval_sort[i] == key_values[j]){
          keyword_sort[i] = keyword_array.get(j);
          key_values[j] = 0;
          break;
        }else{continue;}
      }
    }
    // for(int i=0; i<key_values.length; i++){
    //   System.out.print(key_values[i] + ", ");
    // }
    // System.out.println(", ");
    // for(int i=0; i<keyval_sort.length; i++){
    //   System.out.print(keyval_sort[i] + ", ");
    // }
    // System.out.println(", ");
    // for(int i=0; i<keyword_sort.length; i++){
    //   System.out.print(keyword_sort[i] + ", ");
    // }

  }

//method to determine g values and create hash table
  public void create_Htable(){
    gValue = new int[52];                             //to store gValues
    hTable = new String[keyword_sort.length];         //to store hash table with keywords
    int maxValue = keyword_sort.length/2;             //max value for increasing g values

    Arrays.fill(gValue, 0);                           //setting all elements as 0
    Arrays.fill(hTable, "none");                      //setting all elements as none

    for (int i=0; i<keyword_sort.length; i++ ) {                //looping through sorted keywords
      String word = keyword_sort[i];
      char fletter = word.charAt(0);                            //first letter of the keyword
      char lletter = word.charAt(word.length()-1);              //last letter of the keyword
      int gFirst = get_gValue(fletter);                         //getting g values
      int gLast = get_gValue(lletter);

      for(int j=0; j<(maxValue+1)*2; j++){                      //looping to return to begigng if collison happen
        int h = get_hValue(word.length(), gFirst, gLast);       //getting initial hash value

        if(hTable[h] == "none"){                                //if no collison
          hTable[h] = (String)word;                                     //assign hash table with keyword
          set_gValue(fletter,gFirst);                           //assign relevent gvalue for first letter
          set_gValue(lletter,gLast);                            //assign relevent gvalue for last letter
          break;                                                //breaking out from the loop to get to next word
        }else{
          if(gFirst<5){                                         //if collision happend and first letter g value is < max g value
            gFirst++;                                           //increase by 1
          }else{                                                //if collision happend and first letter g value is = max g value
            gLast++;                                            //increase last letter g value by 1
          }
        }
      }
    }
  }

//method to get g values of letters
  public int get_gValue(char ch){
    int letter = (int)ch;                     //storing chater's int value
    int g=0;

    if(letter>=97 && letter<=122){          // if letter is simple
      g = gValue[letter-97];
    }else if(letter>=65 && letter<=90){     // if letter is capital
      g = gValue[letter-39];
    }
    return g;
  }

//method to set g value of letters
  public void set_gValue(char letter, int value){
    int ltr = (int)letter;

    if(ltr>=97 && ltr<=122){
      gValue[letter-97] = value;
    }else if(ltr>=65 && ltr<=90){
      gValue[letter-39] = value;
    }

  }

//method to get hash values
  public int get_hValue(int length, int gFirst, int gLast){
    int hashValue = (length + gFirst + gLast) % keyword_sort.length;
    return hashValue;
  }

//method to read document and count
  public void read_document(Scanner sc){
    keyword_count = new int[keyword_sort.length];         //to store keyword Count
    Arrays.fill(keyword_count, 0);                        //fiiling array with 0

    while(sc.hasNextLine()){                            //while scanner has a next line
        String line = sc.nextLine();
        if(line.trim().length()!=0){                    //to skipping the blank lines-if line is not a blank line
          lineCount++;                                  //increasing the line count
        }
        String[] words = line.split(" ");               //spliting line by spaces
        wordCount = wordCount + words.length;           //incresing the word count
        count_words(words);                             //invoking count method to count keywords in the line
    }
  }

//method to count keywords
  public void count_words(String words[]){

    for(int i=0; i<words.length; i++){                     //to loop through words array
      int fGvalue=0;                                       //initializing g values as 0 of curren word first and last letters
      int lGvalue=0;
      String word = (String)words[i];                      //storing a word from the array

      try {                                                                 //try block used to avoid errors in case blank line is scaning
        fGvalue = get_gValue(word.charAt(0));                               //getting g values for first and last letter from the g value array
        lGvalue = get_gValue(word.charAt(word.length()-1));
      } catch(Exception e) {
        // System.out.println(e);
      }
      int hValue = get_hValue(word.length(), fGvalue, lGvalue);             //getting hash value of the scanned word

      try {                                                                 //try block to avoid out of bound Exception
        if(hTable[hValue].equals(word)){                                    //if scanned word is equal to hash table word
          keyword_count[hValue] = keyword_count[hValue]+1;                  //increasing keword count by 1
        }
      } catch(Exception e) {
        // System.out.println(e);
      }
    }
    // for(int i=0; i<keyword_count.length; i++){
    //   System.out.println(keyword_count[i]);
    // }

  }

}
