package aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Test {

	public static void main(String[] args) {
		Container container1 = new Container("1", 2L, 3L, 4L);
		Container container2 = new Container("1", 2L, 3L, 4L);
		Container container3 = new Container("2", 3L, 3L, 3L);
		Container container4 = new Container("2", 1L, 2L, 3L);

		List<Container> list = new ArrayList<>();
		list.add(container1);
		list.add(container2);
		list.add(container3);
		list.add(container4);

		Collector<Container, Container, Container> myCol = new Collector<Container, Container, Container>() {

			@Override
			public Supplier<Container> supplier() {
				return Container::new;
			}

			@Override
			public BiConsumer<Container, Container> accumulator() {
				return (A, B) -> {
					Container c1 = (Container) A;
					Container c2 = (Container) B;
					c1.setNum1(c2.getNum1() + c1.getNum1());
					c1.setNum2(c2.getNum2() + c1.getNum2());
					c1.setNum3(c2.getNum3() + c1.getNum3());
				};
			}

			@Override
			public BinaryOperator<Container> combiner() {
				return (A, B) -> {
					Container c1 = (Container) A;
					Container c2 = (Container) B;
					c1.setNum1(c2.getNum1() + c1.getNum1());
					c1.setNum2(c2.getNum2() + c1.getNum2());
					c1.setNum3(c2.getNum3() + c1.getNum3());
					return c1;
				};
			}

			@Override
			public Function<Container, Container> finisher() {
				return Function.identity();
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.unmodifiableSet(
						EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
			}
		};

		ConcurrentMap<String, Container> collect = list.parallelStream()
				.collect(Collectors.groupingByConcurrent(item -> {
					Container c = (Container) item;
					return c.getGroupFeild();
				}, myCol));

		collect.forEach((k, v) -> {
			System.out.println(k);
			System.out.println(v);
		});

	}

	static class Container {
		private String groupFeild;
		private Long num1 = 0L;
		private Long num2 = 0L;
		private Long num3 = 0L;

		public Container() {
		}

		public Container(String groupFeild, long num1, long num2, long num3) {
			this.groupFeild = groupFeild;
			this.num1 = num1;
			this.num2 = num2;
			this.num3 = num3;
		}

		public String getGroupFeild() {
			return groupFeild;
		}

		public void setGroupFeild(String groupFeild) {
			this.groupFeild = groupFeild;
		}

		public Long getNum1() {
			return num1;
		}

		public void setNum1(Long num1) {
			this.num1 = num1;
		}

		public Long getNum2() {
			return num2;
		}

		public void setNum2(Long num2) {
			this.num2 = num2;
		}

		public Long getNum3() {
			return num3;
		}

		public void setNum3(Long num3) {
			this.num3 = num3;
		}

		@Override
		public String toString() {
			return "Container [groupFeild=" + groupFeild + ", num1=" + num1 + ", num2=" + num2 + ", num3=" + num3 + "]";
		}

	}

}
