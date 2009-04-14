How to build the jars from the source tree:

Project is transitioning to maven -- for now, 
use the command line files from this directory to build 

a) the core jar 

and 

b) the source jar to along with it for your IDE.  
Source jar will *not* contain the class files, just .java.  

Output of both will be in /dist/

on unix you'll want to do a 'chmod +x <filename>' on these files and then just ./jar_core_classes
which will build the jredis-alphazero.jar in the ../dist/ directory. 

Thank you for your patience!

