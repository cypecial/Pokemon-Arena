/**
 * @(#)Pokemon.java
 * this is a pokemon class that contains all the access specifiers for individual pokemons, 
 * accessors methods and other useful methods that supports the main PokemonArena Class.
 */
import java.util.*;
public class Pokemon{
	public String name,type,resist,weakness;
	public int initialHp, hp, energy;
	ArrayList<Attack> attacks;
	public boolean pokeStunned=false;
	private boolean successAtt=true, useEnergy=true, disabled=false;
	public Pokemon(String name,int hp, String type, String resist, String weakness,ArrayList<Attack> attacks ){
		this.name=name;
		this.initialHp=hp;//starting health point, will not be effected during battle
		this.hp=initialHp;//current health of pokemon, will be effeced during battle
		this.type=type;
		this.resist=resist;
		this.energy=50;
		this.weakness=weakness;
		this.attacks=attacks;
	}

	public static boolean pokeFaint(Pokemon poke){
		boolean faint=false;
		if(poke.hp<=0){//checks if pokemon has fainted
			faint=true;
		}
		return faint;
	}
	public boolean passFail(){//random coin toss with 50% chance of passing
    	boolean pass = false;
    	Random num = new Random();
		int var=num.nextInt(2);//50% chance for specials
		if(var==0){
			pass = true;
		}
		return pass;
    }
    public void stun(Pokemon myPoke,Pokemon enemy){//used for "stun" special
    	if(passFail()==true){
    		enemy.pokeStunned=true;
			System.out.printf("%s has been stunned, %s pass\n",enemy.name,enemy.name);
		}
		else{
			System.out.printf("%s failed to stun %s\n",myPoke.name,enemy.name);
			pokeStunned=false;
		}
    }
    public void recharge(Pokemon poke){//used for "recharge" special
    	poke.energy+=20;
    	if(poke.energy>50){
			poke.energy=50;
		}
		System.out.printf("%s has recovered 20 energy points\n",poke.name);
    }
	public int randomAtt(Pokemon enemy){;
		Random num = new Random();
		boolean move=false;
		ArrayList<Integer>movesCanUse=new ArrayList<Integer>();
		for(int i=0;i<enemy.attacks.size();i++){
			if(hasEnergy(enemy,i)==true){//if pokemon has enough energy to perform the attack
				move=true;
				movesCanUse.add(i);//add avaliable atts to list
			}
		}
		if(movesCanUse.size()==0){//if pokemon has no energy to perform any attack (pass)
			return -1;
		}
		int newId=num.nextInt(movesCanUse.size());//choose random att of avaliable atts		
		return newId;
	}
	public boolean hasEnergy(Pokemon current, int attId){
		Attack pAtt=current.attacks.get(attId);
		boolean hasEnergy=false;
		if(current.energy>=pAtt.energy){//checks if current energy of pokemon is enough
			hasEnergy=true;				// to perform selected attack
		}
		return hasEnergy;
	}
	public void setEnergy(Pokemon current,int val){
		//perform energy calculation after an attack is used
		current.energy-=val;
		if(current.energy<=0){
			current.energy=0;
		}
	}
	public void wildStorm(Pokemon current,int attId,Pokemon enemy,boolean success){//based off first success
		Attack pAtt=current.attacks.get(attId);
		if(enemy.hp>0){
			if(success==true){
				System.out.printf("%s used %s\n",current.name,pAtt.name);
				successAtt=true;
				useEnergy=false;//only use energy on first wild storm attack
				attCalc(current,attId,enemy);
				wildStorm(current,attId,enemy,passFail());//goes for next wild storm with 50% passing rate
			}	
			else{
				successAtt=false;
				System.out.println("wild storm ended\n");
			}
		}
		else{
			successAtt=false;//does not continue wild storm after enemy faints
			System.out.println("wild storm ended\n");
		}
		
	}
	public void specials(Pokemon current,int attId,Pokemon enemy){//checks for all special attacks
		Attack pAtt=current.attacks.get(attId);
		//stun~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//50% stun rate
		if(pAtt.special.equals("stun")){
			System.out.printf("%s used %s\n",current.name,pAtt.name);
			stun(current,enemy);
		}
		//wild card~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//50% success rate
		else if(pAtt.special.equals("wild card")){
			System.out.printf("%s used %s\n",current.name,pAtt.name);
			if(passFail()==false){
				System.out.printf("%s failed\n",current.name);
				successAtt=false;
				setEnergy(current,pAtt.energy);
			}
			else{
				System.out.println("wild card success!\n");
				successAtt=true;
			}
		}
		//wild storm~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//50% base success rate + 50% free wild storm (no energy cost)
		else if(pAtt.special.equals("wild storm")){
			setEnergy(current,pAtt.energy);
			if(passFail()==false){//failed on first use
				successAtt=false;
				System.out.printf("%s failed to use %s\n",current.name,pAtt.name);
			}
			else{//success on first use
				System.out.println("wild storm activate!\n");
				useEnergy=false;
				wildStorm(current,attId,enemy,true);
			}
		}
		//disable~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		else if(pAtt.special.equals("disable")){
			if(enemy.disabled==true){//can only be disabled once
				System.out.printf("%s has already been disabled\n",enemy.name);
			}
			else{
				System.out.printf("%s used %s\n%s has been disabled\n",current.name,pAtt.name,enemy.name);
				for(int i=0;i<enemy.attacks.size();i++){
					enemy.attacks.get(i).damage-=10;//reduce all enemy attack damage by 10
					if(enemy.attacks.get(i).damage<0){
						enemy.attacks.get(i).damage=0;//cannot go below 0
					}
					disabled=true;
				}
			}
		}	
		//recharge~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~		
		else if(pAtt.special.equals("recharge")){
			System.out.printf("%s used %s\n",current.name,pAtt.name);
			recharge(current);//adds 20 energy to current pokemon
		}
	}
	public void attCalc(Pokemon current,int attId,Pokemon enemy){//effectiveness, damage calculations
		Attack pAtt=current.attacks.get(attId);
		int dmg = pAtt.damage;
		if(successAtt==true){
			if(current.type.equals(enemy.resist)){			
				System.out.println("it is not very effective\n");
				dmg/=2;
			}
			else if(current.type.equals(enemy.weakness)){
				System.out.println("it is very effective!\n");
				dmg*=2;
			}
			enemy.hp-=dmg;
			if(useEnergy==true){
				setEnergy(current,pAtt.energy);//leftover energy after performing attack
			}
		}			
		if(enemy.hp<=0){
			enemy.hp=0;
		}
		//displays current stats of pokemons in battle
		System.out.printf("%-10s: hp: %-3d energy: %-3d\n",current.name,current.hp,current.energy);
		System.out.printf("%-10s: hp: %-3d energy: %-3d\n",enemy.name,enemy.hp,enemy.energy);
	}
	public void pokeAttack(Pokemon current,int attId, Pokemon enemy){
		Attack pAtt=current.attacks.get(attId);
		if(pAtt.special.equals(" ")){//no specials
			System.out.printf("%s used %s\n",current.name,pAtt.name);
		}
		else{//special attacks
			specials(current,attId,enemy);
		}
		if (successAtt==true){
			attCalc(current,attId,enemy);
		}
	}
	public String toString(){
		return String.format("%s: \n |hp: %d | type: %s | resist: %s | weakness: %s\n",name, hp, type, resist, weakness);
	}
}