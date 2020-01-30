import java.util.*;
import java.io.*;

class RandomArray{

	public static void shuffle(Object[] array){
	
		int NBelements=array.length;

		for(int i=0;i<NBelements;i++){
		
			int s=i+(int)(Math.random()*(NBelements-i));
			Object temp=array[s];
			array[s]=array[i];
			array[i]=temp;
			
		}
		
	}
	
}

class Instruction{//INSTRUCTION -----------------------------------------------------------
	Semaphore sem;
	int type;
	int id;
	Instruction suiv;
	
	public Instruction(){
		sem=null;
		type=2;		
		suiv=null;
		id=-1;
	}
	public Instruction(Semaphore s,int t){
		sem=s;
		type=t;		
		suiv=null;
		id=-1;
	}
	public Instruction(Semaphore s,int t,Instruction suivant){
		sem=s;
		type=t;		
		suiv=suivant;
	}
	public Instruction copy(){
		Instruction newe;
		if(suiv==null){
			newe=new Instruction(sem,type,null);
			newe.id=id;
		}
		else {
			newe=new Instruction(sem,type,suiv.copy());
			newe.id=id;
		}
		return newe;
	}
	public String toString(){
		String s = "<"+id+">"; //triche NO_TYPE
		if(type==-1){
			s="P("+sem.id+") ";
		}else if(type==1){
			s="V("+sem.id+") ";
		}else if(type==0){
			s="<SC> ";
		}
		return s;
	
	}
}

class Processus{ //PROCESSUS----------------------------------------------------------------
	Instruction instru;
	int id;
	char type; //L,E OU X, sinon pb
	
	public Processus(){
		this.type=0;
		this.instru=new Instruction();
		id=0;
	}
	public Processus(char type,Instruction I,int i){
		this.type=type;
		this.instru=I;
		this.id=i;
	}
	public Processus copy() {
		Processus P2=new Processus(type,instru.copy(),id);
		return P2;
	}
	public String toString() {
		return("id="+id+":"+type+"/instru="+instru);
	}
		
}

class Semaphore{
	char id; //lettre majuscule pour l'identification du sémaphore
	int compteur;
	Processus [] list;

	public void addBLOQUE (Processus P) {
		Processus[] newlist=new Processus[list.length + 1 ];
		int i=0;
		for(i=0;i<list.length;) {
			newlist[i]=list[i];
			i++;
		}
		newlist[i]=P;
		list=newlist;
	}
	public Semaphore(char id, int compt, Processus [] list){
		this.id=id;
		this.compteur=compt;
		this.list=list;
	}
	public Semaphore(char id){
		this(id,0,new Processus [0]);
	}
	public Semaphore copy(){
		Semaphore s= new Semaphore(id,compteur,list);
		return s;
	}
	public String toString(){
		return("Id :"+id+"\tCompteur :"+compteur);
	}
}

class Lecture{
	
	Processus [] ProcessusList;
	Semaphore [] SemaphoreList;
	Instruction L;
	Instruction E;
	Instruction X;
	int NbSimulation;
	int nbL,nbE,nbX;
	
	
	public Instruction TRASH(Instruction L){
		Instruction memL=L ;
		
		while(L.suiv!=null){
			if(L.suiv.type==2){
				L.suiv=L.suiv.suiv;
			}
			if(L.suiv!=null){L=L.suiv;}
		}
		
		if(memL.type==2)return memL.suiv;
		else return memL;
	}
	public Instruction ConcatenationLinkedList(Instruction L1,Instruction L2){
		Instruction p=L1;
		if(L1==null){
			if(L2==null){return null;}
			else{return L2;}
		}
		else{
			if(L2==null){return L1;}
			else{
				p=L1;
				while(p.suiv!=null){
					p=p.suiv;
				}
				p.suiv=L2;	
			}
		}
		return p;	
	}
	public Instruction ConcatenationLinkedList(Instruction L1,Instruction L2,Instruction L3){
		/*
		Instruction p=new Instruction();
		Instruction mem=p;
		while(L1!=null){
				if(L1.type!=2){
					p.suiv=L1.copy();
					p=p.suiv;
				}
				L1=L1.suiv;
		}
		
		while(L2!=null){
			if(L1.type!=2){p.suiv=L2.copy();p=p.suiv;}
			L1=L1.suiv;
		}
		while(L3!=null){
			if(L1.type!=2){p.suiv=L3.copy();p=p.suiv;}
			L1=L1.suiv;
		}
		
		return mem.suiv;
		*/
		ConcatenationLinkedList(ConcatenationLinkedList(L1,L2),L3);
		return L1;
		
	}
	public void AffichageLinkedList(Instruction L){
		while(L!=null){
			System.out.print("/"+L + L.type);
			L=L.suiv;
		}
		System.out.println(" ");
		return;	
	}
	public int nombre(char [] ch){
		int n=0;
		int i=0;
		//System.out.println(ch);
		while(!(ch[i]<='9' && ch[i]>='0')){
			i++;
			if(i==ch.length){break;}
		}
		while(ch[i]<='9' && ch[i]>='0'){
			n=(n*10+ (int)((ch[i])-'0'));
			i++;
			if(i==ch.length){break;}
		}
		return n; 
	}
	public Instruction SectionCritique(int n){
		if(n<2)return null;
		Instruction start=new Instruction();
		Instruction p=start;
		
		for(int i=0;i<n+1;i++){
			p.type=0;
			p.sem=null;
			p.suiv=new Instruction();
			p=p.suiv;
		}
		return start.suiv;
	}
	public long IterationTotal() {
		long nbiL=0,nbiE=0,nbiX=0;
		
		Instruction p;
		p=L;
		while(p!=null) {nbiL++;p=p.suiv;}
		p=E;
		while(p!=null) {nbiE++;p=p.suiv;}
		p=X;
		while(p!=null) {nbiX++;p=p.suiv;}
		
		return((nbiL*nbL + nbiE*nbE + nbiX*nbX)*NbSimulation);
	}

	public Lecture(String filename){
		this.SemaphoreList = new Semaphore[26];
		
		for(int i=0;i<26;i++){//initialisation liste de semaphores
			this.SemaphoreList[i]=new Semaphore((char)('A'+i));
		}
		//System.out.println("SemaphoreList created");
		
		File file=new File(filename);
		Scanner sc=null;
		
		try{
		
			//System.out.println("debut starters");
			sc=new Scanner(file);
			char [] ligne;
			int type=0;
			int i,k;
			int numligne=0,indice=0;
			nbE=0;nbL=0;nbX=0;
			
			//TIERS DE LISTES
			Instruction PL,PE,PX,EL,EE,EX;
			PL=new Instruction();PE=new Instruction();PX=new Instruction();
			EL=new Instruction();EE=new Instruction();EX=new Instruction();
			Instruction startPL=PL,startPE=PE,startPX=PX,startEL=EL,startEE=EE,startEX=EX;
			//System.out.println("Fin starters");
			
			
			
			while(sc.hasNextLine()){
			
				//System.out.println("new ligne");
				numligne++;
				i=0;
				ligne=(sc.nextLine()).toCharArray();
				
				
				//System.out.print(numligne+":"+type +"----------------");
				
				//AFFICHAGE DE LIGNE
				/*
				while(i<ligne.length){System.out.print((char)ligne[i]);i++;}
				System.out.println();
				i=0;
				*/
				//TEST DE LIGNE VIDE ET AVANCER
				if(ligne.length==0){
					//System.out.println("c'est une ligne vide");
					continue;
				}
				while(ligne[i]==' '|| ligne[i]=='\t'){
					i++;
					if(i==ligne.length){break;}
				}
				if(ligne.length==i){
					//System.out.println("c'est une ligne vide");
					continue;
				}
				//System.out.println("debut de ligne en position :"+i);
					
				//DEBUT DES TESTS	
				if(ligne[i]=='#'){continue;}
				if(ligne[i]=='%'){
					if(ligne[i+1]=='I'&& ligne[i+2]=='N'){
						type=1;continue;
					}
					else if (ligne[i+1]=='P'&&ligne[i+2]=='A'){
						type=2;continue;
					}
					else if (ligne[i+1]=='P'&&ligne[i+2]=='L'){
						type=3;continue;
					}
					else if (ligne[i+1]=='P'&&ligne[i+2]=='E'){
						type=4;continue;
					}
					else if (ligne[i+1]=='P'&&ligne[i+2]=='X'){
						type=5;continue;
					}
					else if (ligne[i+1]=='E'&&ligne[i+2]=='L'){
						type=6;continue;
						
					}
					else if (ligne[i+1]=='E'&&ligne[i+2]=='E'){
						type=7;continue;
					}
					else if (ligne[i+1]=='E'&&ligne[i+2]=='X'){
						type=8;continue;
					}
					else if (ligne[i+1]=='F'&&ligne[i+2]=='I'){
						type=9;break;
					}
					else{System.out.println("% inconnu ligne : "+numligne);}
				}
				
				if(type==1){ //PARTIE INITIALISATION IN declaration de variable;
					//System.out.println("in initialisation");
					if(ligne[i]>='A' && ligne[i]<='Z'){
						if(ligne[i+1]!=':'){System.out.println("Probleme");break;}
						indice=ligne[i]-'A';
						(this.SemaphoreList[indice]).id=ligne[i]; // completement inutile
						(this.SemaphoreList[indice]).compteur=nombre(ligne);
						//System.out.println(SemaphoreList[indice]);
						
						
					}
				}
				if(type==2){//CRÉATION DE LA LISTE DE PROCESSUS (RÉPARTITION EN FONCTION DE L, E, OU X)
					if(ligne[i]>'A' && ligne[i]<'Z'){
						if(ligne[i+1]!=':'){System.out.println("Probleme ligne :"+numligne);break;}
						
						if(ligne[i]=='L'){
							nbL=nombre(ligne);
						}else if(ligne[i]=='E'){
							nbE=nombre(ligne);
						}else if(ligne[i]=='X'){
							nbX=nombre(ligne);
						}else if(ligne[i]=='N'){
							this.NbSimulation=nombre(ligne);
						}else{System.out.println("Invalid ligne: "+numligne);}
									
					}	
				}
				if(type==3){
					if((ligne[i]=='P' || ligne[i]=='V') && ligne[i+1]=='(' && ligne[i+3]==')' && ligne[i+2]<='Z' && ligne[i+2]>='A'){
						PL.sem=SemaphoreList[ligne[i+2]-'A'];
						if(ligne[i]=='P'){PL.type= -1;}
						if(ligne[i]=='V'){PL.type=  1;}
						PL.suiv=new Instruction();
						PL=PL.suiv;
					}else{System.out.println("Erreur de P(_) ou V(_) ligne: "+numligne);continue;}
				}
				if(type==4){
					if((ligne[i]=='P' || ligne[i]=='V') && ligne[i+1]=='(' && ligne[i+3]==')' && ligne[i+2]<='Z' && ligne[i+2]>='A'){
						PE.sem=SemaphoreList[ligne[i+2]-'A'];
						if(ligne[i]=='P'){PE.type= -1;}
						if(ligne[i]=='V'){PE.type=  1;}
						PE.suiv=new Instruction();
						PE=PE.suiv;
					}else{System.out.println("Erreur de P(_) ou V(_) ligne: "+numligne);continue;}
				}	
				if(type==5){
					if((ligne[i]=='P' || ligne[i]=='V') && ligne[i+1]=='(' && ligne[i+3]==')' && ligne[i+2]<='Z' && ligne[i+2]>='A'){
						PX.sem=SemaphoreList[ligne[i+2]-'A'];
						if(ligne[i]=='P'){PX.type= -1;}
						if(ligne[i]=='V'){PX.type=  1;}
						PX.suiv=new Instruction();
						PX=PX.suiv;
					}else{System.out.println("Erreur de P(_) ou V(_) ligne: "+numligne);continue;}
				}	
				if(type==6){
					if((ligne[i]=='P' || ligne[i]=='V') && ligne[i+1]=='(' && ligne[i+3]==')' && ligne[i+2]<='Z' && ligne[i+2]>='A'){
						EL.sem=SemaphoreList[ligne[i+2]-'A'];
						if(ligne[i]=='P'){EL.type= -1;}
						if(ligne[i]=='V'){EL.type=  1;}
						EL.suiv=new Instruction();
						EL=EL.suiv;
					}else{System.out.println("Erreur de P(_) ou V(_) ligne: "+numligne);continue;}
				}	
				if(type==7){
					if((ligne[i]=='P' || ligne[i]=='V') && ligne[i+1]=='(' && ligne[i+3]==')' && ligne[i+2]<='Z' && ligne[i+2]>='A'){
						EE.sem=SemaphoreList[ligne[i+2]-'A'];
						if(ligne[i]=='P'){EE.type= -1;}
						if(ligne[i]=='V'){EE.type=  1;}
						EE.suiv=new Instruction();
						EE=EE.suiv;
					}else{System.out.println("Erreur de P(_) ou V(_) ligne: "+numligne);continue;}
				}	
				if(type==8){
					if((ligne[i]=='P' || ligne[i]=='V') && ligne[i+1]=='(' && ligne[i+3]==')' && ligne[i+2]<='Z' && ligne[i+2]>='A'){
						EX.sem=SemaphoreList[ligne[i+2]-'A'];
						if(ligne[i]=='P'){EX.type= -1;}
						if(ligne[i]=='V'){EX.type=  1;}
						EX.suiv=new Instruction();
						EX=EX.suiv;
					}else{System.out.println("Erreur de P(_) ou V(_) ligne: "+numligne);continue;}
				}	
			}
			
		
		//ASSEMBALGE DES LISTES
		//GRANDES LISTES
		
		//System.out.println("CONCATENATION");
		this.L=TRASH(ConcatenationLinkedList(startPL,SectionCritique(50),startEL));
		this.E=TRASH(ConcatenationLinkedList(startPE,SectionCritique(50),startEE));
		this.X=TRASH(ConcatenationLinkedList(startPX,SectionCritique(50),startEX));
		//System.out.println("LinkedList Instructions créés");
		
		//VERIFICATION si il y a autant de P() que de V()
		Instruction p;
		p=L;
		int [] egalisation= new int ['Z'];
		while(p!=null) { 
			if(p.type==1) {egalisation[p.sem.id]++;}
			if(p.type==-1) {egalisation[p.sem.id]--;}
			p=p.suiv;
		}
		boolean verifL=true;
		for(int h=0;h<egalisation.length;h++) {
			if(egalisation[h]!=0) {verifL=false;break;}
		}
		if(!verifL)System.out.println("PAS AUTANT DE P(X) que de V(X) dans L");
		
		p=E;
		egalisation= new int ['Z'];
		while(p!=null) { 
			if(p.type==1) {egalisation[p.sem.id]++;}
			if(p.type==-1) {egalisation[p.sem.id]--;}
			p=p.suiv;
		}
		boolean verifE=true;
		for(int h=0;h<egalisation.length;h++) {
			if(egalisation[h]!=0) {verifE=false;break;}
		}
		if(!verifE)System.out.println("PAS AUTANT DE P(X) que de V(X) dans E");
		
		p=X;
		egalisation= new int ['Z'];
		while(p!=null) { 
			if(p.type==1) {egalisation[p.sem.id]++;}
			if(p.type==-1) {egalisation[p.sem.id]--;}
			p=p.suiv;
		}
		boolean verifX=true;
		for(int h=0;h<egalisation.length;h++) {
			if(egalisation[h]!=0){verifX=false;break;}
		}
		if(!verifX)System.out.println("PAS AUTANT DE P(X) que de V(X) dans X");
		
		if(!(verifL && verifE && verifX))return;
		//else System.out.println("bon nombre de P et V");
		
		//AJOUT D'INDICE AU INSTRUCTIONS
		
		int s=0;
		Instruction toto=L;
		while(toto!=null) {
			toto.id=s;
			s++;
			toto=toto.suiv;
		}
		
		s=0;
		toto=E;
		while(toto!=null) {
			toto.id=s;
			s++;
			toto=toto.suiv;
		}
		
		s=0;
		toto=X;
		while(toto!=null) {
			toto.id=s;
			s++;
			toto=toto.suiv;
		}
		
		//FIN DE RECUPERATION DES DONNEES	
		
		this.ProcessusList=new Processus[nbL+nbE+nbX];
		for(k=0;k<nbL;k++){
			ProcessusList[k]=new Processus('L',L.copy(),k);
		}
		for(k=nbL;k<nbL+nbE;k++){
			ProcessusList[k]=new Processus('E',E.copy(),k);
		}
		for(k=nbL+nbE;k<nbL+nbE+nbX;k++){
			ProcessusList[k]=new Processus('X',X.copy(),k);
		}
		//System.out.println("ProcessusList créé et partiellement initialisé");
				
		
		
		}catch(IOException e){
			System.out.println("FIle_IO_error");
			e.printStackTrace();
		}finally{
			if(sc!=null) sc.close();
		}
	}
	public void AffichageLecture() {
		System.out.println("Semaphores :");
		for(int i=0;i<26;i++){
			if((SemaphoreList[i]).compteur==0)continue;
			System.out.println(SemaphoreList[i]);
		}
		System.out.println("AFFICHAGE des Instructions");
		System.out.print("L=");
		AffichageLinkedList(L);	
		System.out.print("E=");
		AffichageLinkedList(E);	
		System.out.print("X=");
		AffichageLinkedList(X);
		
		System.out.println("AFFICHAGE des Processus");
		for(int i=0; i<ProcessusList.length ; i++) {
			System.out.print((ProcessusList[i]).type);
		}
		System.out.println(" ");
		
	}
}
class Memory{
	int [] L;
	int [] E;
	int [] X;
	
	public Memory() {
		L = new int [0];
		E = new int [0];
		X = new int [0];
	}
	public Memory(int n) {
		L = new int [n];
		E = new int [n];
		X = new int [n];
	}
	public boolean inMemory(int l, int e, int x) {
		for(int i=0; i<L.length;i++) {
			if(L[i]==l && E[i]==e && X[i]==x) {return(true);}
		}
		return(false);
	}
	public boolean inMemory(int [] T) {
		//System.out.println("in memory:"+T[0]+" "+T[1]+" "+T[2]+"---"+L.length);
		for(int i=0; i<L.length;i++) {
			if(L[i]==T[0] && E[i]==T[1] && X[i]==T[2]) {return(true);}
		}
		return(false);
	}
	public Memory addtoMemory(int l,int e, int x) {
		Memory M=new Memory(L.length + 1);
		int i=0;
		for(i=0; i<L.length;) {
			M.L[i]=L[i];
			M.E[i]=E[i];
			M.X[i]=X[i];
			i++;
		}
		M.L[i]=l;
		M.E[i]=e;
		M.X[i]=x;
		//this.AffichageMemory();
		return(M);
	}
	public Memory addtoMemory(int [] T) {
		Memory M=new Memory(L.length + 1);
		//System.out.println("M -> "+ M.L.length);
		int i=0;
		for(i=0; i<L.length;i++) {
			M.L[i]=L[i];
			M.E[i]=E[i];
			M.X[i]=X[i];
		}
		M.L[M.L.length-1]=T[0];
		M.E[M.L.length-1]=T[1];
		M.X[M.L.length-1]=T[2];
		//M.AffichageMemory();
		return(M);
	}	
	public void AffichageMemory() {
		if(L.length==0)System.out.println("aucune possibilite");
		System.out.println("|L|E|X|");
		System.out.println("|-----|");
		for(int i=0; i<L.length;i++) {
			System.out.println("|"+L[i]+"|"+E[i]+"|"+X[i]+"|");
		}
		return;
	}

}
class SIMULATION{
	Memory memory;
	long NINSTRU;

	
	public Processus[] add(Processus [] L, Processus P) { //ne retourne pas de copie
		if(P.equals(null))return L;
		Processus [] newL=new Processus[L.length+1];
		int i=0;
		for(i=0; i<L.length;i++) {
			if(L[i].equals(P)) {
				System.out.println("DEJA DEDANS : ."+L[i]);
				return L;
			}
			newL[i]=L[i];
		}
		newL[newL.length-1]=P;
		return(newL);
	}
	public Processus[] remove (Processus [] L, Processus P) {
		if(L.length==0) {
			return new Processus [0];
		}
		if(L.length==1) {
			if(L[0].equals(P)) {
				Processus [] K = new Processus[0];
				return K;
			}else {
				System.out.println("Bug equals OU remove impossible car pas ici");
				return L;
			}
		}
		Processus [] newL=new Processus [L.length-1];
		int i=0;
		while(!L[i].equals(P) && i<L.length-1) {
			//System.out.println(i+" "+L[i]);
			newL[i]=L[i];
			i++;
		}
		while(i<L.length-1) {
			//System.out.println(i+"_"+L[i+1]);
			newL[i]=L[i+1];
			i++;
		}
		return(newL);		
	}
	public boolean AllInstructionToNull(Processus [] list) {
		for(int i=0 ; i<list.length ; i++) {
			if(list[i].instru != null)return false;
		}
		return true;	
	}
	public Processus ChoisirUnProcessusReady (Processus [] list) { //LA LISTE NE DOIT PAS ETRE VIDE
		if(list.equals(null)) {System.out.println("return null");return null;}
		if(list.length<=0) {System.out.println("return null");return null;}
		Random rn = new Random();
		int i = rn.nextInt() % list.length;
		if(i<0)i=-i;
		Processus P=list[i];
		//System.out.println(P);
		return P;
	}
	
	public int[] EtatCritique (Processus [] ALL) { //Combien de chaque sont en <SC>
		int [] T=new int [3];
		T[0]=0;T[1]=0;T[2]=0;
		//System.out.println("etat critique");
		for(int i=0; i<ALL.length;i++) {
			if(ALL[i].instru!=null) {
				//System.out.println(i+"// processus :"+ALL[i]+"//type : "+ALL[i].instru.type+"//instru.id="+ALL[i].instru.id);
				//System.out.println("LALALA ---> "+ALL[i]);
				if(ALL[i].instru.type ==0) {
					     if(ALL[i].type=='L') {T[0]++;}
					else if(ALL[i].type=='E') {T[1]++;}
					else if(ALL[i].type=='X') {T[2]++;}
				}
			}else {
				//System.out.println(i+"//"+ALL[i]+"// fini");
			}
		}
		//System.out.println(T[0]+" "+T[1]+" "+T[2]+"\n");
		return T;
	}
	
	public Processus [] Exe(Processus [] ALL, Processus [] READY, Processus p){
		if(p==null){return READY;}
		if(p.instru!=null){
		if(p.instru.sem!=null) {
			System.out.print(NINSTRU+":"+p.instru.sem.compteur+":"+p.instru.sem.id+":");
			for(int i=0; i<p.instru.sem.list.length;i++)System.out.println(p.instru.sem.list[i]);
			System.out.println("");
		}}
		Random rn = new Random();
		int NBI = 0;
		while(NBI>5 || NBI<1) {NBI = (rn.nextInt() % 5)+1;if(NBI<0)NBI=-NBI;}
		//System.out.println("Exe : "+NBI);
		int n=0;
		int [] T ;
		Processus p2;
		
		
		
		for(n=0;n<NBI;n++) {
			if(p.instru==null) {// Le processus est terminé donc il existe plus nulle part			
				return READY;
			}
			//System.out.println("Processus:"+p.id+" type:"+p.instru.type +" n°"+p.instru.id+" SEMAPHORE:"+p.instru.sem);
			if(p.instru.type==2) {// c'etait pas normal
				System.out.println("c'etait pas normal");
				p.instru=p.instru.suiv;
				continue;
			}			
			if(p.instru.type==0) {
				NINSTRU++;
				p.instru=p.instru.suiv;
				//System.out.println(p.id+" <SC> "+p.instru.id);
			}
			
			else if(p.instru.type==-1){
				//System.out.println(p.id+" <P("+p.instru.sem.id+")> "+p.instru.id);
				if(p.instru.sem.compteur > 0){
					p.instru.sem.compteur--;
					NINSTRU++;p.instru=p.instru.suiv;
				}
				else if(p.instru.sem.compteur == 0){
					//System.out.println("ADD ICI to liste sem bloque");
					p.instru.sem.list=add(p.instru.sem.list,p);
					//System.out.println("==0 "+p.instru.sem+"---"+p+" && taille="+p.instru.sem.list.length+" ! "+p.instru.type);
					/*System.out.println("APRES ADDITION:");
					for(int i=0; i<p.instru.sem.list.length; i++) {
						System.out.println(p.instru.sem.list[i]);
					}*/
					return READY;
				}
				else {System.out.println("big bad problem");return READY;}
			}
			else if(p.instru.type==1) { // V
				//System.out.println(p.id+" <V("+p.instru.sem.id+")> "+p.instru.id);
				
				if(p.instru.sem.list.length == 0 ){
					p.instru.sem.compteur++;
					NINSTRU++;
					p.instru=p.instru.suiv;
				}
				else {
					Semaphore S=p.instru.sem;
					NINSTRU++;p.instru=p.instru.suiv;
					//System.out.println("ADD ICI pas bloque mais LIBERE");
					READY=add(READY,p);
					
					p2=ChoisirUnProcessusReady(S.list);
					S.list=remove(S.list,p2);
					NINSTRU++;
					p2.instru=p2.instru.suiv;
					if(p2.instru==null);
					READY=Exe(ALL,READY,p2);					
					return READY;
				}
			}			
		}
		T=EtatCritique(ALL);
		if(!memory.inMemory(T)){
			//System.out.println("ajout en memoire");
			//System.out.println(T[0]+":"+T[1]+":"+T[2]+":"+memory.inMemory(T));
			memory=memory.addtoMemory(T);
		}
		//System.out.println("ADD ICI pas fini bloqué ou libere");
		READY=add(READY,p); //on en a fini avec lui
		/*
		System.out.print("\nREADY in exe de fin fin : "+NINSTRU);
		for(int i=0; i<READY.length; i++) {
			System.out.print("|"+READY[i]);
		}System.out.println("|");
		*/
		
		return READY;
	}
	
	public SIMULATION(Lecture l) {
	int [] memTOTALsemlist=new int [26];
	boolean READYVIDE=false;
	for(int i=0;i<26;i++) {
		memTOTALsemlist[i]=l.SemaphoreList[i].compteur;
		//if(semlist[i].compteur!=0)System.out.println(semlist[i]);
	}
	
	Processus [] READY;
	Processus [] ALL;
	
	memory=new Memory(0);
	int nbSimulation=l.NbSimulation;
	int idsimulation, evolv=0;
	Processus p=new Processus();
	/*int [] T;*/
	for(idsimulation=1; idsimulation<=nbSimulation ; idsimulation++ ) {
		if(READYVIDE) {
			System.out.println("il y a eu un probleme a la simulation precedante");
			for(int i=0;i<26;i++) {
				l.SemaphoreList[i].compteur = memTOTALsemlist[i];
				//if(semlist[i].compteur!=0)System.out.println(semlist[i]);
			}
			READYVIDE=false;
		}
		READY=new Processus[l.ProcessusList.length];
		ALL=new Processus[l.ProcessusList.length];
		
		for(int i=0; i<l.ProcessusList.length; i++) {
			READY[i]=l.ProcessusList[i].copy();
			//System.out.println(READY[i]+"---"+READY[i].instru.sem);
			ALL[i]=READY[i]; //on ne copie pas pour garder le lien
		}
		
		//debut de la ieme simulation 
		if(idsimulation==evolv || idsimulation==1 || idsimulation==2) {
			System.out.println("Lancement simulation n°"+idsimulation+"--------------------------");
			evolv+=(nbSimulation/10) ;
		}
		while(!AllInstructionToNull(ALL)) {
			//System.out.println("Choix d'un nouveau pocessus, NB="+NINSTRU);
			if(READY.length==0) {
				READYVIDE=true;
				System.out.println("READY VIDE pas nomal :"+idsimulation);
				break;
			}
			else {
				//System.out.print("\nREADY pré : "+NINSTRU);for(int i=0; i<READY.length; i++) {System.out.print("|"+READY[i]);}System.out.println("|");
				p=ChoisirUnProcessusReady(READY);
				READY=remove(READY,p);
				
				//System.out.println("Processus elu :"+p.id+" ;il reste :"+READY.length);
				//System.out.print("\nREADY post remove and pick : "+NINSTRU);for(int i=0; i<READY.length; i++) {System.out.print("|"+READY[i]);}System.out.println("|");
				
				READY=Exe(ALL,READY,p); //le remet dans ready si processus p pas fini
				//System.out.print("\nREADY post exe : "+NINSTRU);for(int i=0; i<READY.length; i++) {System.out.print("|"+READY[i]);}System.out.println("|");
				//T=EtatCritique(ALL);if(!memory.inMemory(T))memory.addtoMemory(T);	
			}
		}
	}
	return;
	}
}

public class Projet6{
	
	public static void main(String[] args){
		
		System.out.println("DEBUT PROGRAMME");
		System.out.println("DEBUT DE LECTURE");
		Lecture l= new Lecture("test.txt");
		System.out.println("FIN DE LECTURE\n");
		l.AffichageLecture();		
		System.out.println("\nDEBUT DE SIMUALTION on attent a obtenir:"+l.IterationTotal()+"instructions executees");
		SIMULATION S=new SIMULATION(l);
		System.out.println("FIN DE SIMUALTION : "+S.NINSTRU+"instructions realises");
		System.out.println("AFFICHAGE DES POSSIBILITES");
		S.memory.AffichageMemory();
		System.out.println("FIN AFFICHAGE");
		System.out.println("FIN PROGRAMME");	
		
	}		
}


