/**
 * @(#)PokemonArena.java
 *
 * @author Yiping Che
 * @version 1.00 2012/11/22
 * This is a simple game of Pokemon with no mind-blowing graphics. The
 * goal is to defeat all of the enemy Pokemon to be declared <Trainer Supreme>.
 */
import java.util.*;
import java.io.*;
public class PokemonArena {  
	public static final int NAME=0, HP=1, TYPE=2, RESIST=3, WEAKNESS=4, NUMATT=5;
	static ArrayList<Pokemon>allPoke=new ArrayList<Pokemon>();// holds all pokemons
	static ArrayList<Pokemon>selected=new ArrayList<Pokemon>();//holds users selected pokemons
	static ArrayList<Pokemon>enemies=new ArrayList<Pokemon>();//holds all enemy pokemons
	static Scanner Kb = new Scanner(System.in);
	
  	public static void main(String[]args)throws IOException{
    	Scanner PokeFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
    	
    	int num = Integer.parseInt(PokeFile.nextLine());//number of lines in the text file
	    while(PokeFile.hasNextLine()){	//splits the pokemon text file
	    	ArrayList<Attack> attacks= new ArrayList<Attack>();    
	    	String line = PokeFile.nextLine();
	    	String[]poke= line.split(",");
	    	int numAtt=Integer.parseInt(poke[NUMATT]);
			for(int i=1;i<=numAtt;i++){    		
	    		Attack pkAtt=new Attack(poke[i*4+2],Integer.parseInt(poke[i*4+3]),Integer.parseInt(poke[i*4+4]),poke[i*4+5]);
	    		attacks.add(pkAtt); //adds all attacks for the pokemon
			}
			Pokemon x=new Pokemon(poke[NAME],Integer.parseInt(poke[HP]),poke[TYPE],poke[RESIST],poke[WEAKNESS], attacks);
			allPoke.add(x);//adds all pokemon info
			enemies.add(x);//adds pokemon for future use
	    }
	    System.out.println("Welcome to Pokemon Arena! \nPlease choose your 4 starting pokemons: \n");
	    for(int i=1;i<=4;i++){
	    	System.out.printf("Pokemon %d:\n",i);
	    	ChoosePokemon(allPoke);//choose initial 4 pokemons
	    }
	 	Collections.shuffle(enemies);//enemy appearance are random
		for(int i=0;i<enemies.size();i++){
			System.out.printf("\nWild %s has appeared! \n",enemies.get(i).name);
			battle(i);//battle every enemy pokemon
			if(gameOver()==true){
				System.out.println("All user pokemons have fainted, game over!");
				break;
			}
			else{
				for(int p=0;p<4;p++){
					if(selected.get(p).hp>0){//heals non-fainted pokemons only
						selected.get(p).hp+=20;
						if(selected.get(p).hp>selected.get(p).initialHp){//does not heal over initial hp
							selected.get(p).hp=selected.get(p).initialHp;
						}	
					}				
				}
			}
			System.out.println("all of your non-fainted pokemons have recovered 20 hp\n");
		}
		if(gameOver()==false){
			System.out.println("Congradulations! You have defeated all enemy pokemons. You have earned the title <Trainer Supreme>!\n"); 
		}		
  	}
  	public static int isValid(int range1, int range2, int input){
  		if(input<range1 || input >range2){//if user input is not within range
  			return 0;
  		}
  		else{
  			return input;
  		}
  	}
  	public static void ChoosePokemon(ArrayList<Pokemon>allPokes){//choses starting pokemons
		for(int i=0;i<allPoke.size();i+=3){//organize in columns of three's
			if(allPoke.size()>3){
				System.out.printf("%-2d  %-12s \t%-2d  %-12s \t%-2d  %-12s \n",(i+1),allPokes.get(i).name,(i+2),allPokes.get(i+1).name,(i+3),allPokes.get(i+2).name);
			}//display all pokemon names
		}
		int choice = Kb.nextInt();//if choice invalid
		if(isValid(1,allPoke.size(),choice)==0){
			System.out.println("Please choose a pokemon listed \n");
			ChoosePokemon(allPoke);
		}
		else{
			System.out.println(allPoke.get(choice-1));//display pokemon stats
			System.out.println("Attacks:\n");
			for(int i=0;i<allPoke.get(choice-1).attacks.size();i++){//display pokemon attacks
				System.out.println(allPoke.get(choice-1).attacks.get(i).toString());
			}
			System.out.println("are you sure you want to use this pokemon? \n 1.yes \t 2.no \n");
			int yes_no=Kb.nextInt();
			while(isValid(1,2,yes_no)==0){
				System.out.println("please choose a valid option");
				System.out.println("are you sure you want to use this pokemon? \n 1.yes \t 2.no \n");
				yes_no=Kb.nextInt();
			}
			if(yes_no==1){
				if(selected.contains(allPoke.get(choice-1))){//repeat choices
					System.out.println("you may not choose the same pokemon twice!\n");
					ChoosePokemon(allPoke);
				}
				selected.add(allPoke.get(choice-1));//add selected pokemons
				enemies.remove(allPoke.get(choice-1));//remove selected pokemon from enemies list
			}
			if(yes_no==2){
				ChoosePokemon(allPoke);
			}
		}
	}
	public static int choosePoke(ArrayList<Pokemon> selected){//choose one of 4 pokemon to use in battle
		System.out.println("Which pokemon do you wish to use?\n");
		System.out.printf("\n1. %-5s \t 2. %-5s \t 3. %-5s \t 4. %-5s\n",selected.get(0).name,selected.get(1).name,selected.get(2).name,selected.get(3).name);
		int pkmn = Kb.nextInt();
		while(isValid(1,4,pkmn)==0){
			System.out.println("choice invalid! please choose from listed pokemons\n");
			System.out.printf("\n1. %-5s \t 2. %-5s \t 3. %-5s \t 4. %-5s\n",selected.get(0).name,selected.get(1).name,selected.get(2).name,selected.get(3).name);
			pkmn = Kb.nextInt();
		}
		return pkmn-1;	
	}
	public static int start(){;//choose who starts first
		Random num = new Random();
		int newId=num.nextInt(2);//if 0, user start, if 1, enemy start
		return newId;
	}
	public static void battle(int index){// index = current enemy in battle
		int pkmn = choosePoke(selected);
		Pokemon myPoke = selected.get(pkmn);
		Pokemon enemyPoke = enemies.get(index);
		if(myPoke.pokeFaint(myPoke)==true){
			System.out.printf("%s has already fainted, please choose another pokemon\n",myPoke.name);
			battle(index);
		}
		else{		
			System.out.printf("%s, I choose you! \n",myPoke.name);
			if(start()==0){			
				System.out.println("user start");
				while(myPoke.pokeFaint(myPoke)==false){
					if(enemyPoke.pokeFaint(enemyPoke)==false){//battle does not continue after enemy dies
						myPokeAtt(myPoke,enemyPoke,index);
						if(enemyPoke.pokeStunned==true){
							enemyPoke.pokeStunned=false;//resets stun
							recoverEnergy();
							myPokeAtt(myPoke,enemyPoke,index);
						}
						if(enemyPoke.pokeFaint(enemyPoke)==true){
							System.out.printf("%s fainted\n",enemyPoke.name);
							recoverEnergy();
							break;
						}
						else{
							enemyAtt(myPoke,enemyPoke,index);
							if(myPoke.pokeFaint(myPoke)==true){
								System.out.printf("%s fainted\n",myPoke.name);
								if(gameOver()==true){//if all user pokemons have fainted
									break;
								}
								else{
									recoverEnergy();
									battle(index);//choose another pokemon
								}
							}														
						}
						
					}
					if(enemyPoke.pokeFaint(enemyPoke)==false && myPoke.pokeFaint(myPoke)==false){
						recoverEnergy();
					}										
				}				
								
			}
			else{//enemy start				
				System.out.println("enemy start");
				while(enemyPoke.pokeFaint(enemyPoke)==false){
					int attId = enemyPoke.randomAtt(enemyPoke);
					if(attId==-1){//no energy to perform any attack (pass)
						System.out.printf("%s has no energy left to attack, %s pass\n",enemyPoke.name,enemyPoke.name);
						myPokeAtt(myPoke,enemyPoke,index);
						if(enemyPoke.pokeFaint(enemyPoke)==true){
							System.out.printf("%s fainted\n",enemyPoke.name);
							break;
						}
						else{
							enemyAtt(myPoke,enemyPoke,index);
							if(enemyPoke.pokeStunned==true){
								enemyPoke.pokeStunned=false;
								recoverEnergy();
								enemyAtt(myPoke,enemyPoke,index);
							}
							if(myPoke.pokeFaint(myPoke)==true){
								System.out.printf("%s fainted\n",myPoke.name);
								if(gameOver()==true){
									break;
								}
								else{
									recoverEnergy();
									battle(index);
								}
							}														
						}
					}
					else{
						enemyPoke.pokeAttack(enemyPoke,attId, myPoke);
					}
					if(myPoke.pokeFaint(myPoke)==true){
						System.out.printf("%s fainted\n",myPoke.name);
						if(gameOver()==true){
							break;
						}
						else{
							recoverEnergy();
							battle(index);
						}
					}
					else{
						myPokeAtt(myPoke,enemyPoke,index);

						if(enemyPoke.pokeFaint(enemyPoke)==true){
							System.out.printf("%s fainted\n",enemyPoke.name);
							recoverEnergy();
						}
					}
					if(enemyPoke.pokeFaint(enemyPoke)==false && myPoke.pokeFaint(myPoke)==false){
						recoverEnergy();
					}
				}
				
			}
		}
  	}
	public static void myPokeAtt(Pokemon myPoke, Pokemon enemyPoke, int index){
		System.out.printf("what will %s do? \n",myPoke.name);
		System.out.printf("1. Attack \n2. Retreat \n3. Pass\n");
		int arp = Kb.nextInt();
		while(isValid(1,3,arp)==0){
			System.out.println("please choose a valid option\n");
			System.out.printf("1. Attack \n2. Retreat \n3. Pass\n");
			arp=Kb.nextInt();
		}
		if(arp==1){//attack
			for(int i=1;i<=myPoke.attacks.size();i++){//print att choices
				System.out.println(i+". "+myPoke.attacks.get(i-1));
			}					
									
			System.out.printf("which attack will %s use? \n",myPoke.name);//choose att
			int att=Kb.nextInt();
		
			while(isValid(1,myPoke.attacks.size(),att)==0){
				System.out.println("please choose a valid option\n");
				System.out.printf("which attack will %s use? \n",myPoke.name);
					att=Kb.nextInt();
			}
			if(myPoke.hasEnergy(myPoke,att-1)==false){
				System.out.printf("%s has no energy left to perform this attack\n",myPoke.name);
				myPokeAtt(myPoke,enemyPoke,index);
			}
			else{//has energy to att
				myPoke.pokeAttack(myPoke,att-1,enemyPoke);
				if(enemyPoke.pokeStunned==true){//enemy cannot attack
					enemyPoke.pokeStunned=false;
					recoverEnergy();
					myPoke.pokeAttack(myPoke,att-1,enemyPoke);
				}
										
			}				
		}
		if(arp==2){//retreat (choose another pokemon)
			System.out.printf("%s, come back!\n",myPoke.name);
			battle(index);
		}
		if(arp==3){//pass (recover energy)
			System.out.printf("%s pass\n",myPoke.name);
			enemyAtt(myPoke,enemyPoke,index);
			recoverEnergy();
		}
	}
	public static void enemyAtt(Pokemon myPoke, Pokemon enemyPoke, int index){
		int attId = enemyPoke.randomAtt(enemyPoke);//choose random attack
		if(attId==-1){//if enemy has no energy to perform any attack (pass)
			System.out.printf("%s has no energy left to attack, %s pass\n",enemyPoke.name,enemyPoke.name);
			myPokeAtt(myPoke,enemyPoke,index);
		}
		else{
			enemyPoke.pokeAttack(enemyPoke,attId, myPoke);
			if(myPoke.pokeStunned==true){
				myPoke.pokeStunned=false;
				if(myPoke.pokeFaint(myPoke)==true){
					System.out.printf("%s fainted",myPoke.name);
					if(gameOver()==false){
						recoverEnergy();
						battle(index);
					}
				}
				else{
					recoverEnergy();
					enemyAtt(myPoke,enemyPoke,index);
				}
			}		
		}
	}
  	public static void recoverEnergy(){
		for(int i=0; i<allPoke.size(); i++){
			allPoke.get(i).energy+=10;//all pokemon heals 10 energy points
			if(allPoke.get(i).energy>50){//max energy healed does not exceed 50
				allPoke.get(i).energy=50;
			}
		}
		System.out.println("all pokemons have recovered 10 energy points\n");
	} 
	public static boolean gameOver(){//checks game over conditions
		boolean gameOver=false;
		Pokemon myPoke1=selected.get(0);
		Pokemon myPoke2=selected.get(1);
		Pokemon myPoke3=selected.get(2);
		Pokemon myPoke4=selected.get(3);
		if(myPoke1.hp==0 && myPoke2.hp==0 && myPoke3.hp==0 && myPoke3.hp==0){
			gameOver=true;//if all user pokemons have fainted
		}
		return gameOver;
	}   
}