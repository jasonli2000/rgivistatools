CMDTEST0 ;
 ;
 Q
 ;
DO ;
 D L0^R0,L1^R1(A,B,C),L2^R3(.A,,.C)
 D @FIND0,@LIND1^@RINDI1
 D:TCOND0 T0,T1:COND1,T2
 DO:TCOND1 T2(A,,)
 D T0:COND0,T1:COND1 DO:TCOND2
 . D @FIND1^R4
 . D L4^@RIND4
 DO:TCOND3
 . D T5+5^R5,@LIND5+3,@LIND6+3^@RIND6
 . D 2+1^R6,3+1,7+5^R6,T8^R8
 . D L8+4^@RIND8
 D:TCOND4
 . D ^R10
 . D ^@RIND10
 D:TCOND5
 . DO &Y0,&X1^Y1 D &PK2.Y2
 . D &PK3.X3^Y3
 D:TCOND6
 . D &E0(P1,P2),&D1^E1(P1,.P2)
 . DO &PK2.E2(P3,@PIND)
 . D &PK3.D3^E3(P5)
 D:CONDENV
 . D LE0^[ENV1,ENV2]RE0,L1^|ENV1|RE1(A,B,C),LE2^[ENV3]RE3(.A,,.C)
 . D ^[ENV4]RE4:COND7
 . DO T5+5^[ENV8]R5,@LIND5+3^|ENV9|R9
 Q
 ;
GOTO ;
 G L0^R0,L1^R1,L2^R2
 G @FIND0,@LIND1^@RINDI1
 G:TCOND0 T0,T1:COND1,T2
 GOTO:TCOND1 T2
 G T0:COND0,T1:COND1 DO:TCOND2
 . G @FIND1^R4
 . G L4^@RIND4
 DO:TCOND3
 . G T5+5^R5,@LIND5+3,@LIND6+3^@RIND6
 . G 2+1^R6,3+1,7+5^R6,T8^R8
 . G L8+4^@RIND8
 D:TCOND4
 . G ^R10
 . G ^@RIND10
 D:CONDENV
 . G LE0^[ENV1,ENV2]RE0,L1^|ENV1|RE1,LE2^[ENV3]RE3
 . G ^[ENV4]RE4:COND7
 . GOTO T5+5^[ENV8]R5,@LIND5+3^|ENV9|R9
 GOTO DE^RE:TCOND G ARZ
 Q
 ;