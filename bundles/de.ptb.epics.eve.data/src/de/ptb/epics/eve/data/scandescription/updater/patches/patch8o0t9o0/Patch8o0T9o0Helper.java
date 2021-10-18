package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.ptb.epics.eve.data.ComparisonTypes;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class Patch8o0T9o0Helper {

	/**
	 * Finds event pairs which define a hysteresis (e.g. x < 5 with Action ON and
	 * x > 3 with Action OFF), combines them to a pause condition, adds it to 
	 * the return list and adds the used events to the given used set.
	 * @param eventList the eventList to search in
	 * @param usedSet a set used events are added to
	 * @return a list of pause conditions (with hysteresis definitions)
	 */
	public List<PseudoPauseCondition> findHysteresis(List<PauseEvent> eventList, 
			Set<PauseEvent> usedSet) {
		List<PseudoPauseCondition> pauseConditions = new ArrayList<>();
		for (PauseEvent eventA : eventList) {
			if (eventA.getAction().equals(EventAction.ON)) {
				for (PauseEvent eventB : eventList) {
					if (eventA != eventB &&
							eventA.getId().equals(eventB.getId()) &&
							eventB.getAction().equals(EventAction.OFF) &&
							isOperatorCompatibleHysteresis(eventA, eventB)) {
						usedSet.add(eventA);
						usedSet.add(eventB);
						pauseConditions.add(this.combineEvents(eventA, eventB));
					}
				}
			}
		}
		return pauseConditions;
	}
	
	/*
	 * Returns true if operator of event A is > and operator of event B is < or
	 * vice versa. 
	 */
	protected boolean isOperatorCompatibleHysteresis(PauseEvent eventA, PauseEvent eventB) {
		return ((eventA.getLimit().getComparison().equals(ComparisonTypes.GT) &&
				 eventB.getLimit().getComparison().equals(ComparisonTypes.LT)) ||
				(eventA.getLimit().getComparison().equals(ComparisonTypes.LT) &&
				 eventB.getLimit().getComparison().equals(ComparisonTypes.GT)));
	}
	
	/**
	 * Finds event subsets (e.g. x < 5 and x < 3), combines them to a pause 
	 * condition, adds it to the return list and adds the used events to the 
	 * given used set.
	 * @param eventList the eventList to search in
	 * @param usedSet a set used events are added to
	 * @return a list of pause conditions
	 */
	public List<PseudoPauseCondition> findSubsets(List<PauseEvent> eventList, 
			Set<PauseEvent> usedSet) {
		List<PseudoPauseCondition> pauseConditions = new ArrayList<>();
		for (PauseEvent eventA : eventList) {
			if (usedSet.contains(eventA)) {
				continue;
			}
			for (PauseEvent eventB : eventList) {
				if (eventA != eventB &&
						isOperatorCompatibleSubset(eventA, eventB) &&
						isActionCompatibleSubset(eventA, eventB)) {
					usedSet.add(eventA);
					usedSet.add(eventB);
					pauseConditions.add(this.combineEvents(eventA, eventB));
				}
			}
		}
		return pauseConditions;
	}
	
	/*
	 * Returns true if operators of both are equal and < or > (not eq or ne).
	 */
	protected boolean isOperatorCompatibleSubset(PauseEvent eventA, PauseEvent eventB) {
		return eventA.getLimit().getComparison().equals(eventB.getLimit().getComparison()) &&
				(eventA.getLimit().getComparison().equals(ComparisonTypes.GT) ||
					eventB.getLimit().getComparison().equals(ComparisonTypes.LT));
	}
	
	/*
	 * Returns true if actions of both are equals and not OFF.
	 */
	protected boolean isActionCompatibleSubset(PauseEvent eventA, PauseEvent eventB) {
		return eventA.getAction().equals(eventB.getAction()) &&
				!eventA.getAction().equals(EventAction.OFF);
	}
	
	/*
	 * Precondition: expects that given events evaluate to true for 
	 *   (isOperatorCompatibleHysteresis ||
	 *   isOperatorCompatibleSubset && isActionCompatibleSubset)
	 *   
	 * Combines events
	 *   in case of equal operators (subset)
	 *     for int/double: takes the lower/higher value for LT/GT
	 *     for datetime/string: takes the value of eventA
	 *   in case of LT/GT or GT/LT (hysteresis)
	 *     takes value of eventA for pauselimit and value of eventB for continue limit
	 *        and operator from eventA
	 */
	protected PseudoPauseCondition combineEvents(PauseEvent eventA, PauseEvent eventB) {
		if (!(isOperatorCompatibleHysteresis(eventA, eventB) ||
				isOperatorCompatibleSubset(eventA, eventB) && 
				isActionCompatibleSubset(eventA, eventB))) {
			return null;
		}
		if (eventA.getLimit().getComparison().equals(eventB.getLimit().getComparison())) {
			// combine subset
			PseudoPauseCondition pauseCondition = new PseudoPauseCondition();
			pauseCondition.setDeviceId(eventA.getId());
			pauseCondition.setOperator(eventA.getLimit().getComparison());
			pauseCondition.setType(eventA.getLimit().getType());
			switch (eventA.getLimit().getType()) {
			case DATETIME:
				pauseCondition.setPauseLimit(eventA.getLimit().getValue());
				break;
			case DOUBLE:
				double a = Double.parseDouble(eventA.getLimit().getValue());
				double b = Double.parseDouble(eventB.getLimit().getValue());
				if (pauseCondition.getOperator().equals(ComparisonTypes.LT)) {
					pauseCondition.setPauseLimit(Double.toString(Math.min(a, b)));
				} else {
					pauseCondition.setPauseLimit(Double.toString(Math.max(a, b)));
				}
				break;
			case INT:
				int c = Integer.parseInt(eventA.getLimit().getValue());
				int d = Integer.parseInt(eventB.getLimit().getValue());
				if (pauseCondition.getOperator().equals(ComparisonTypes.LT)) {
					pauseCondition.setPauseLimit(Integer.toString(Math.min(c, d)));
				} else {
					pauseCondition.setPauseLimit(Integer.toString(Math.max(c, d)));
				}
				break;
			case STRING:
				pauseCondition.setPauseLimit(eventA.getLimit().getValue());
				break;
			default:
				break;
			}
			return pauseCondition;
		} else {
			// combine hysteresis
			PseudoPauseCondition pauseCondition = new PseudoPauseCondition();
			pauseCondition.setDeviceId(eventA.getId());
			pauseCondition.setOperator(eventA.getLimit().getComparison());
			pauseCondition.setType(eventA.getLimit().getType());
			pauseCondition.setPauseLimit(eventA.getLimit().getValue());
			pauseCondition.setContinueLimit(eventB.getLimit().getValue());
			return pauseCondition;
		}
	}
	
	protected PseudoPauseCondition convert(PauseEvent pauseEvent) {
		if (pauseEvent.getAction().equals(EventAction.OFF)) {
			return null;
		}
		PseudoPauseCondition pauseCondition = new PseudoPauseCondition();
		pauseCondition.setDeviceId(pauseEvent.getId());
		pauseCondition.setType(pauseEvent.getLimit().getType());
		pauseCondition.setOperator(pauseEvent.getLimit().getComparison());
		pauseCondition.setPauseLimit(pauseEvent.getLimit().getValue());
		return pauseCondition;
	}
}
