/*
 * DtwTest.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package org.clueminer.dtw.run;

import org.clueminer.timeseries.TimeSeries;
import org.clueminer.util.DistanceFunction;
import org.clueminer.util.DistanceFunctionFactory;
import org.clueminer.dtw.TimeWarpInfo;


public class DtwTest
{

   // PUBLIC FUNCTIONS
   public static void main(String[] args)
   {
      if (args.length!=2 && args.length!=3)
      {
         System.out.println("USAGE:  java DtwTest timeSeries1 timeSeries2 [EuclideanDistance|ManhattanDistance|BinaryDistance]");
         System.exit(1);
      }
      else
      {
         final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
         final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
         
         final DistanceFunction distFn;
         if (args.length < 3)
         {
            distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance"); 
         }
         else
         {
            distFn = DistanceFunctionFactory.getDistFnByName(args[2]);
         }   // end if
         
         final TimeWarpInfo info = org.clueminer.dtw.DTW.getWarpInfoBetween(tsI, tsJ, distFn);

         System.out.println("Warp Distance: " + info.getDistance());
         System.out.println("Warp Path:     " + info.getPath());
      }  // end if

   }  // end main()

}  // end class DtwTest
