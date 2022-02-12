# dbms-test

### closure.kt

example of interaction with the program:

```haskell
? enter functional dependencies (one on each line):
   A   ->   B   C
 E   ->  C  F
B ->   E
C  D -> E F
exit
CommonPack {
    CommonRule {At(B)->At(E)}
    CommonRule {At(A)->At(B),At(C)}
    CommonRule {At(E)->At(C),At(F)}
    CommonRule {At(C),At(D)->At(E),At(F)}
}

? enter the set of attributes you want to close:
   A     B
exit
AttributePack {At(A),At(B)}

! response, closure:
AttributePack {At(A),At(B),At(C),At(E),At(F)}

! check, here are all the attributes:
[A, B, C, D, E, F]
```
