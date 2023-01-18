package DG.DA;

import java.util.stream.IntStream;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SOLVE_LP_ORTOOLS {

	public void SOLVE_LP_ORTOOLS() {

	}

	private static final Logger logger = Logger.getLogger(SOLVE_LP_ORTOOLS.class.getName());

	public static List<List<String>> SolveOrToolsLP(Integer[][] duration, List<MaintenanceWorkDTO>  workDTO,
			int depot1VehicleCount, int depot2VehicleCount, List<List<Integer>> depots,
			int depotCount) {
		Loader.loadNativeLibraries();
		int numberOfNodes = duration[0].length;
		double maxTime = 12 * 60 * 60;
		double minTime = 0 * 60 * 60;
		int numberOfVehicles = depot1VehicleCount + depot2VehicleCount;
		int[] allNodes = IntStream.range(0, numberOfNodes).toArray();
		int[] allTasks = IntStream.range(depotCount, numberOfNodes).toArray();
		int[] allVehicles = IntStream.range(0, numberOfVehicles).toArray();
		int[] allDepots = IntStream.range(0, depotCount).toArray();

//		int[] switches = {2};
//		ArrayList<Integer> vehiclesThatCannotMaintainSwitches = new ArrayList<>();
//
//		//check if we need to solve for one or two depots
//		if (depot1VehicleCount > 0) {
//			vehiclesThatCannotMaintainSwitches.add(0);
//		}

		MPSolver model = MPSolver.createSolver("SCIP");
		model.enableOutput();

		logger.info("Defining model...");
		MPVariable[][][] x = new MPVariable[numberOfNodes][numberOfNodes][numberOfVehicles];
		for (int k : allVehicles) {
			for (int i : allNodes) {
				for (int j : allNodes) {
					if (i != j) {
						x[i][j][k] = model.makeBoolVar(String.format("x%d_%d, %d", i, j, k));
					}
				}
			}
		}

		// constraint 1: Leave every task at most once
		logger.info("Creating " + String.valueOf(numberOfNodes) + "Constraint 1...");
		for (int j : allTasks) {
			MPConstraint constraint = model.makeConstraint(0, 1, "c1");
			for (int i : allNodes) {
				for (int k : allVehicles) {
					if (i != j) {
						constraint.setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}

		// constraint 2: reach every task from at most one other task
		logger.info("Creating " + String.valueOf(numberOfNodes) + "Constraint 2...");
		for (int i : allTasks) {
			MPConstraint constraint = model.makeConstraint(0, 1, "c2");
			for (int j : allNodes) {
				for (int k : allVehicles) {
					if (i != j) {
						constraint.setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}

		// constraint 3: depart from own depot
		logger.info("Creating " + String.valueOf(numberOfNodes) + "Constraint 3.1....");
		for (int i : allDepots) {
			List<Integer> cars = depots.get(i);
			for (int car : cars) {
				MPConstraint constraint0 = model.makeConstraint(1, 1, "c3.1.");
				for (int j : allNodes) {
					constraint0.setCoefficient(x[i][j][car], 1);
				}

				MPConstraint constraint1 = model.makeConstraint(1, 1, "c3.2.");
				for (int j : allNodes) {
					constraint1.setCoefficient(x[j][i][car], 1);
				}
			}
		}

		// constraint 4: only routes starting from a group's own depot a
		logger.info("Creating Constraint 4...");
		for (int depotIndex : allDepots) {
			List<Integer> cars = depots.get(depotIndex);
			for (int otherDepot : IntStream.range(0, depotCount).toArray()) {
				if (depotIndex != otherDepot) {
					for (int k = 0; k < cars.size(); k++) {
						int car = cars.get(k);
						MPConstraint constraint0 = model.makeConstraint(0, 0, "c4");
						for (int j : allNodes) {
							constraint0.setCoefficient(x[otherDepot][j][car], 1);
						}
						MPConstraint constraint1 = model.makeConstraint(0, 0, "c4");
						for (int i : allNodes) {
							constraint1.setCoefficient(x[i][otherDepot][car], 1);
						}
					}
				}
			}
		}

		// constraint 5: number of vehicles in and out of a tasks's location stays the
		// same
		logger.info("Creating Constraint 5...");
		for (int k : allVehicles) {
			for (int j : allNodes) {
				MPConstraint constraint = model.makeConstraint(0, 0, "c5");
				for (int i : allNodes) {
					if (i != j) {
						constraint.setCoefficient(x[i][j][k], 1);
						constraint.setCoefficient(x[j][i][k], -1);
					}
				}
			}
		}

		// constraint 6: the time-capacity of each vehicle should not exceed the maximum
		// capacity
		logger.info("Creating " + String.valueOf(numberOfNodes) + "Constraint 6...");
		for (int k : allVehicles) {
			MPConstraint constraint = model.makeConstraint(minTime, maxTime, "c6");
			for (int i : allNodes) {
				for (int j : allNodes) {
					constraint.setCoefficient(x[i][j][k], (duration[i][j] + workDTO.get(j).demand));
				}
			}
		}

		// definition of auxilliary variable u
		MPVariable[] u = new MPVariable[numberOfNodes];
		for (int i : allTasks) {
			u[i] = model.makeIntVar(1, numberOfNodes, String.format("u_i%d", i));
		}
//		
		// MTZ subtour elimination
		logger.info("Creating " + String.valueOf(numberOfNodes) + "Constraint 7...");
		for (int i : allTasks) {
			for (int j : allTasks) {
				for (int k : allVehicles) {
					if (i != j) {
						MPConstraint constraint = model.makeConstraint(-numberOfNodes+1, numberOfNodes,
								"c7 " + String.format("%d_%d", i, j));
						constraint.setCoefficient(u[j], 1);
						constraint.setCoefficient(u[i], -1);
						constraint.setCoefficient(x[i][j][k], -numberOfNodes);
					}
				}
			}
		}

		//constraint 7: Comptetency constraint for Structural devices and Switches
//		for(int i : switches) {
//			for (int k: vehiclesThatCannotMaintainSwitches) {
//				for (int j : allNodes) {
//					if (i != j) {
//						MPConstraint constraint7 = model.makeConstraint(0, 0, "c7");
//						constraint7.setCoefficient(x[i][j][k], 1);
//					}
//				}
//			}
//		}

		MPObjective objective = model.objective();
		for (int k : allVehicles) {
			for (int i : allNodes) {
				for (int j : allNodes) {
					if (i != j) {
						objective.setCoefficient(x[i][j][k], 1);
					}
				}
			}
		}
		objective.setMaximization();

		//model.setTimeLimit(300 * 1000);

		String test = model.exportModelAsLpFormat();
		//System.out.println(test);

		MPSolver.ResultStatus resultStatus = model.solve();

		List<List<String>> routesAsString = new ArrayList<List<String>>(numberOfVehicles);
		for (int k = 0; k < numberOfVehicles; k++) {
			routesAsString.add(new ArrayList<String>());
		}

		// Check that the problem has a feasible solution.
		if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
			System.out.println("Total number of nodes visited: " + objective.value() + "/" + String.valueOf(numberOfNodes + depotCount) + "\n");
			for (int k : allVehicles) {
				for (int i : allNodes) {
					for (int j : allNodes) {
						if (i != j && x[i][j][k].solutionValue() == 1.0) {
							routesAsString.get(k).add(String.format("%d_%d", i, j));
						}
					}
				}
			}
		} else {
			System.err.println("No solution found.");
		}

		if (depot1VehicleCount > 0) {

			int current = 0;
			for (int i = 0; i < depot1VehicleCount; i++) {
				// orderCorrectly takes and array and startDepot as arguments
				System.out.println("the array " + i + " is hence: ");
				System.out.println(Utils.orderCorrectly(routesAsString.get(i), "0"));
				current++;
			}

			for (int i = 0; i < depot2VehicleCount; i++) {
				// orderCorrectly takes and array and startDepot as arguments
				System.out.println("the array " + current + " is hence: ");
				System.out.println(Utils.orderCorrectly(routesAsString.get(current), "1"));
				current++;
			}
//			for (int i = 0; i < depot2VehicleCount; i++) {
//				// orderCorrectly takes and array and startDepot as arguments
//				System.out.println("the array " + current + " is hence: ");
//				System.out.println(routesAsString.get(current));
//				current++;
//			}

		} else {
			for (int i = 0; i < depot2VehicleCount; i++) {
				// orderCorrectly takes and array and startDepot as arguments
				System.out.println("the array " + i + " is hence: ");
				System.out.println(Utils.orderCorrectly(routesAsString.get(i), "0"));
			}
		}
		
		return routesAsString;
	}
}
