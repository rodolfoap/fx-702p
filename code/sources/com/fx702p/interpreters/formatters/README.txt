We want to use JDK 1.5 to run on more platforms, including Windows 98, previous versions of MacOSX... 
But we also need the support of several rounding methods in DecimalFormat and it is available only in JDK 1.6.

As usually, it is not possible to simply derive an existing class and report of few lines of codes because Sun cannot even 
write a decimal formatter without using sun.xxx classes, package only interfaces and members and so on... 

i.e we once again are facing pseudo "object-oriented" code which can only be extended the way the designer imagined it. 
And he imagination was not his strong poing.

So we copy the part of the package we need.

Dirty but quick.