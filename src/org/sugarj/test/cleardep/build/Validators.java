package org.sugarj.test.cleardep.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.sugarj.test.cleardep.CompilationUnitTestUtils.set;
import static org.sugarj.test.cleardep.build.SimpleBuildUtilities.inputToFileList;

import java.util.List;

import org.sugarj.common.path.RelativePath;

public class Validators {

	public static interface Validator {
		public void validate();
	}
	
	public static class ElementValidatorBuilder<T> {
		
		private T elem;
		private ListValidatorBuilder<T> parent;
		
		
		
		public ElementValidatorBuilder(ListValidatorBuilder<T> parent, T elem) {
			super();
			this.parent = parent;
			this.elem = elem;
		}



		public Validator before(final T... otherElems) {
			return new Validator() {
				public void validate(){
					for (T otherElem : otherElems) {
					int beforeIndex = parent.list.indexOf(elem);
					int afterIndex = parent.list.indexOf(otherElem);
					
					assertTrue(beforeIndex != -1);
					assertTrue(afterIndex != -1);
					assertTrue(
							elem + " not before "
									+ otherElem, beforeIndex < afterIndex);
					}
				}
			};
		}
		
	}
 	
	public static class ListValidatorBuilder<T> {
		private List<T> list;

		
		private ListValidatorBuilder(List<T> list) {
			super();
			this.list = list;
		};
		
		public ElementValidatorBuilder<T> is(T elem) {
			return new ElementValidatorBuilder<T>(this,elem);
		}
		
		public Validator isEmpty() {
			return new Validator() {
				
				@Override
				public void validate() {
					assertTrue("List is not empty", list.isEmpty());
				}
			};
		}
		
		public Validator containsSameElements(final T... elems) {
return new Validator() {
				
				@Override
				public void validate() {
			assertEquals("list does not contains the expected elements", set(list), set(elems));
				}};
		}
	}
	
	
	
	public static void validateThat(Validator validator) {
		validator.validate();
	}
	public static <T>ListValidatorBuilder<T> in(List<T> list) {
		return new ListValidatorBuilder<>(list);
	}
	
	public static <T>ListValidatorBuilder<T> list(List<T> list) {
		return new ListValidatorBuilder<>(list);
	}
	public static <T>ListValidatorBuilder<T> in(ListValidatorBuilder<T> t) {
		return t;
	}
	
	
	public static ListValidatorBuilder<RelativePath> requiredFilesOf(		TrackingBuildManager manager) {
		return new ListValidatorBuilder<>(inputToFileList(manager.getRequiredInputs()));
	}
	
	public static ListValidatorBuilder<RelativePath> executedFilesOf(		TrackingBuildManager manager) {
		return new ListValidatorBuilder<>(inputToFileList(manager.getExecutedInputs()));
	}
	
}
