package de.ptb.epics.eve.viewer.views.deviceinspectorview;

/**
 * @author Marcus Michalsky
 * @since 1.25.2
 */
public enum PVStatus {
	LIMIT_POSITIVE {
		@Override
		public String toString() {
			return "Limit (+)";
		}
	},
	HOME {
		@Override
		public String toString() {
			return "Home";
		}
	},
	PROBLEM {
		@Override
		public String toString() {
			return "Problem";
		}
	},
	LIMIT_NEGATIVE {
		@Override
		public String toString() {
			return "Limit (-)";
		}
	},
	SOFT_LIMIT {
		@Override
		public String toString() {
			return "Soft-Limit";
		}
	},
	IDLE {
		@Override
		public String toString() {
			return "Idle";
		}
	},
	MOVING {
		@Override
		public String toString() {
			return "Moving";
		}
	}
}