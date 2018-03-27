# SpBproject
The Spark Beyond exam

Part 1:
This part is done without any additional assumptions from my side.
Input is checked as required.
src -> main -> scala - MaxWords.scala is the object holds main code
src -> test -> scala - MaxWords is the test class, that run MaxWords object and place input parameters
 
Part 2:
Here I do have assumptions:
I almost do not check input, I assume input is proper and come as following : 
    <num_words> <timeStart - timeStop> <optional_debug>
    
src -> main -> scala - LogsParser.scala is the object holds main code
src -> test -> scala - LogsParserTest is the test class, that run LogsParser object and place input parameters
Note: logs directory is hardcoded value "/var/log" that resides on LogsParser.scala (change it there if you need)

Common:
1. I used to run any tests class via IntelliJ Shift + F10
2. Both tests were running on directory tree which includes :
   - files with text
   - empty files
   - zipped files with text
   - zipped empty files with text
   - directories with files
   - empty directories
   
Restrictions:
1. Cant care of zipped directories
2. Exception java.nio.charset.MalformedInputException: Input length = 1 is fired for invalid UTF-8 file content, 
I've failed to prevent it or catch it, examples that I've found are pretty misleading
