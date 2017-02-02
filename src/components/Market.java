package components;

/**
 * Created by eva on 25/01/2017.
 */
public class Market {
   public double S;
   double Depth;

   //S is the price of the asset, linear impact function. market depth parameter needs to be thoroughly researched, this is
    //a completely random guess.

    public Market(double S){
        this.S=S;
        this.Depth=100;
    }

    public void updateMarket(double q){
        //this doesnt make any sense!
        System.out.println("The market is stepping.");
        System.out.println("The order has been: "+q );
        double S_=Math.max(S+S*q/Depth, 0.2);
        System.out.println("New Price is: "+S_);
        this.S=S_;

    }

    public void setS(double S){
        this.S=S;
    }
}
