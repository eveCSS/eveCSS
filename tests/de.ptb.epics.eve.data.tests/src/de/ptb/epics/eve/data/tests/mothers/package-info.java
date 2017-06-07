/**
 * Contains factories able to birth different model objects which could be used 
 * as fixtures in unit tests, i.e. manages the lifecycle of test objects 
 * including creation, customization and, when necessary, deletion.
 * 
 * ObjectMothers contain two types of methods:
 * <ul>
 *   <li>creation methods: return all manner of business objects</li>
 *   <li>attachment methods: assist in tailoring the created objects</li>
 * </ul>
 * 
 * Both creation methods and attachment methods are public, so that they may be 
 * used by tests and more complex creation methods.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 * @see <a href="http://martinfowler.com/articles/mocksArentStubs.html">"Mocks Aren't Stubs"</a> by Martin Fowler
 * @see <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.18.4710">Object Mother</a> by Peter Schuh and Stephanie Punke
 * @see <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.100.2927&rank=1">Mock Roles, Not Objects</a> by Steve Freeman , Nat Pryce , Tim Mackinnon , Joe Walnes
 */
package de.ptb.epics.eve.data.tests.mothers;