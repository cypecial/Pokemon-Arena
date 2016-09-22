/**
 * @(#)Attack.java
 *
 * this is an attack class that contains all the access specifiers for individual pokemon attacks.
 */

public class Attack {
	String name, special;
	int energy, damage;	
    public Attack(String name, int energy, int damage, String special) {
    	this.name=name;
    	this.energy=energy;
    	this.damage=damage;
    	this.special=special;
    }
    public String toString(){
    	return String.format("|name: %-10s | energy: %-5d | damage: %-5s | special: %-10s |\n",name, energy, damage, special);
    }    
}