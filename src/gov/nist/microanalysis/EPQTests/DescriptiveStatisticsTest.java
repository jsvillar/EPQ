package gov.nist.microanalysis.EPQTests;

import gov.nist.microanalysis.Utility.DescriptiveStatistics;
import junit.framework.TestCase;

/**
 * <p>
 * Tests the DescriptiveStatistics class.
 * </p>
 * <p>
 * Copyright: Pursuant to title 17 Section 105 of the United States Code this
 * software is not subject to copyright protection and is in the public domain
 * </p>
 * <p>
 * Institution: National Institute of Standards and Technology
 * </p>
 * 
 * @author Nicholas
 * @version 1.0
 */
public class DescriptiveStatisticsTest
   extends TestCase {
   public DescriptiveStatisticsTest(String test) {
      super(test);
   }

   public void testOne() {
      final int[] data = {
         112,
         118,
         132,
         129,
         121,
         135,
         148,
         148,
         136,
         119,
         104,
         118,
         115,
         126,
         141,
         135,
         125,
         149,
         170,
         170,
         158,
         133,
         114,
         140,
         145,
         150,
         178,
         163,
         172,
         178,
         199,
         199,
         184,
         162,
         146,
         166,
         171,
         180,
         193,
         181,
         183,
         218,
         230,
         242,
         209,
         191,
         172,
         194,
         196,
         196,
         236,
         235,
         229,
         243,
         264,
         272,
         237,
         211,
         180,
         201,
         204,
         188,
         235,
         227,
         234,
         264,
         302,
         293,
         259,
         229,
         203,
         229,
         242,
         233,
         267,
         269,
         270,
         315,
         364,
         347,
         312,
         274,
         237,
         278,
         284,
         277,
         317,
         313,
         318,
         374,
         413,
         405,
         355,
         306,
         271,
         306,
         315,
         301,
         356,
         348,
         355,
         422,
         465,
         467,
         404,
         347,
         305,
         336,
         340,
         318,
         362,
         348,
         363,
         435,
         491,
         505,
         404,
         359,
         310,
         337,
         360,
         342,
         406,
         396,
         420,
         472,
         548,
         559,
         463,
         407,
         362,
         405,
         417,
         391,
         419,
         461,
         472,
         535,
         622,
         606,
         508,
         461,
         390,
         432
      };
      final DescriptiveStatistics rs = new DescriptiveStatistics();
      for(final int element : data)
         rs.add(element);
      assertEquals(rs.average(), 280.2986111, 0.001);
      assertEquals(rs.variance(), 14291.97333, 0.001);
      assertEquals(rs.skewness(), 0.577068235, 1.0e-6);
      assertEquals(rs.kurtosis(), -0.393772171, 1.0e-6);
   }
}
