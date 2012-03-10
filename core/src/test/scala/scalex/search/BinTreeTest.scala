package scalex
package search

class BinTreeTest extends ScalexTest {

  //trait Functor[F[_]] {
  //def fmap[A, B](f: A ⇒ B): F[A] ⇒ F[B]
  //}

  //object FunctorList extends Functor[List] {
  //def fmap[A, B](f: A ⇒ B): List[A] ⇒ List[B] = list ⇒ list map f
  //}

  //trait Pointed[F[_]] {
  //def point[A](a: ⇒ A): F[A]
  //}

  //object PointedList extends Pointed[List] {
  //def point[A](a: ⇒ A) = List(a)
  //}

  //trait PointedFunctor[F[_]] {
  //val functor: Functor[F]
  //val pointed: Pointed[F]

  //def point[A](a: ⇒ A): F[A] = pointed.point(a)

  //def fmap[A, B](f: A ⇒ B): F[A] ⇒ F[B] = functor.fmap(f)
  //}

  //object PointedFunctorList extends PointedFunctor[List] {
  //val functor = FunctorList
  //val pointed = PointedList
  //}

  //trait Applic[F[_]] {
  //def applic[A, B](f: F[A ⇒ B]): F[A] ⇒ F[B]
  //}

  //object ApplicList extends Applic[List] {
  //def applic[A, B](f: List[A ⇒ B]): List[A] ⇒ List[B] = list ⇒
  //f zip list map { case (f, v) ⇒ f(v) }
  //}

  //trait Applicative[F[_]] {
  //val pointedFunctor: PointedFunctor[F]
  //val applic: Applic[F]

  //def fmap[A, B](f: A ⇒ B): F[A] ⇒ F[B] = pointedFunctor.fmap(f)
  //def point[A](a: ⇒ A): F[A] = pointedFunctor.point(a)
  //def apply[A, B](f: F[A ⇒ B]): F[A] ⇒ F[B] = applic.applic(f)
  //}

  //implicit object ApplicativeList extends Applicative[List] {
  //val pointedFunctor = PointedFunctorList
  //val applic = ApplicList
  //}

  //implicit object ApplicativeOption extends Applicative[Option] {
  //val pointedFunctor = new PointedFunctor[Option] {
  //val pointed = new Pointed[Option] {
  //def point[A](a: ⇒ A) = Some(a)
  //}
  //val functor = new Functor[Option] {
  //def fmap[A, B](f: A ⇒ B): Option[A] ⇒ Option[B] = option ⇒ option map f
  //}
  //}
  //val applic = new Applic[Option] {
  //def applic[A, B](f: Option[A ⇒ B]): Option[A] ⇒ Option[B] = option ⇒ for {
  //a ← f
  //b ← option
  //} yield a(b)
  //}
  //}

  //trait Traversable[T[_]] {
  //def traverse[F[_]: Applicative, A, B](f: A ⇒ F[B]): T[A] ⇒ F[T[B]]
  //}

  //def BinaryTreeIsTraversable[A]: Traversable[BinaryTree] = new Traversable[BinaryTree] {

  //def createLeaf[B] = (n: B) ⇒ (Leaf(n): (BinaryTree[B]))
  //def createBin[B] = (nl: BinaryTree[B]) ⇒
  //(nr: BinaryTree[B]) ⇒ (Bin(nl, nr): BinaryTree[B])

  //def traverse[F[_]: Applicative, A, B](f: A ⇒ F[B]): BinaryTree[A] ⇒ F[BinaryTree[B]] = (t: BinaryTree[A]) ⇒ {
  //val applicative = implicitly[Applicative[F]]
  //t match {
  //case Leaf(a) ⇒ applicative.apply(applicative.point(createLeaf[B]))(f(a))
  //case Bin(l, r) ⇒
  //applicative.apply(applicative.apply(applicative.point(createBin[B]))(traverse[F, A, B](f).apply(l))).
  //apply(traverse[F, A, B](f).apply(r))
  //}
  //}
  //}

  //[>* Const is a container for values of type M, with a "phantom" type A <]
  //case class Const[M, +A](value: M)

  //implicit def ConstIsPointed[M: Monoid] = new Pointed[({ type l[A] = Const[M, A] })#l] {
  //def point[A](a: ⇒ A) = Const[M, A](implicitly[Monoid[M]].z)
  //}

  //implicit def ConstIsFunctor[M: Monoid] = new Functor[({ type l[A] = Const[M, A] })#l] {
  //def fmap[A, B](f: A ⇒ B) = (c: Const[M, A]) ⇒ Const[M, B](c.value)
  //}

  //implicit def ConstIsApplic[M: Monoid] = new Applic[({ type l[A] = Const[M, A] })#l] {
  //def applic[A, B](f: Const[M, A ⇒ B]) = (c: Const[M, A]) ⇒ Const[M, B](implicitly[Monoid[M]].append(f.value, c.value))
  //}

  //implicit def ConstIsPointedFunctor[M: Monoid] = new PointedFunctor[({ type l[A] = Const[M, A] })#l] {
  //val functor = Functor.ConstIsFunctor
  //val pointed = Pointed.ConstIsPointed
  //}

  //implicit def ConstIsApplicative[M: Monoid] = new Applicative[({ type l[A] = Const[M, A] })#l] {
  //val pointedFunctor = PointedFunctor.ConstIsPointedFunctor
  //val applic = Applic.ConstIsApplic
  //}

  //import scalaz._
  //import Scalaz._
  //import Applicative._

  //sealed trait BinaryTree[A]
  //case class Leaf[A](a: A) extends BinaryTree[A]
  //case class Bin[A](left: BinaryTree[A], right: BinaryTree[A]) extends BinaryTree[A]

  //implicit def BinaryTreeTraverse: Traverse[BinaryTree] = new Traverse[BinaryTree] {
    //def createLeaf[B] = (n: B) ⇒ (Leaf(n): (BinaryTree[B]))
    //def createBin[B] = (nl: BinaryTree[B]) ⇒
    //(nr: BinaryTree[B]) ⇒ (Bin(nl, nr): BinaryTree[B])
    //def traverse[F[_]: Applicative, A, B](f: A ⇒ F[B], ta: TreeBinary[A]): F[TreeBinary[B]] = {
      //val trav = (t: BinaryTree[A]) ⇒ traverse[F, A, B](f, t)
      //val cons = (x: B) ⇒ (xs: Stream[BinaryTree[B]]) ⇒ node(x, xs)
      //val a = implicitly[Applicative[F]]
      //ta match {
        //case Leaf(a) => a.apply(a.pure(createLeaf[B]), f(a))
      //a.apply(a.fmap(f(ta.rootLabel), cons), TraversableTraverse[Stream].traverse[F, Tree[A], Tree[B]](trav, ta.subForest))
    //}
  //}

  //def traverse[F[_]: Applicative, A, B](f: A ⇒ F[B]): BinaryTree[A] ⇒ F[BinaryTree[B]] = (t: BinaryTree[A]) ⇒ {
    //val applicative = implicitly[Applicative[F]]
    //t match {
      //case Leaf(a) ⇒ applicative.apply(applicative.point(createLeaf[B]))(f(a))
      //case Bin(l, r) ⇒
        //applicative.apply(applicative.apply(applicative.point(createBin[B]))(traverse[F, A, B](f).apply(l))).
          //apply(traverse[F, A, B](f).apply(r))
    //}
  //}

  //val tree: BinaryTree[Int] = Bin(
    //Bin(
      //Leaf(1),
      //Leaf(2)
    //),
    //Leaf(3)
  //)

  ////def contents[A]: T[A] ⇒ List[A] = {
  ////val f = (a: A) ⇒ Const[List[A], Any](List(a))
  ////(ta: T[A]) ⇒ traverse[({ type l[U] = Const[List[A], U] })#l, A, Any](f).apply(ta).value
  ////}
  ////def traverse[F[_]: Applicative, A, B](f: A ⇒ F[B], BinaryTree[A])

  //"bin tree" in {

    //"traverse" in {
      //val f = (i: Int) ⇒ List(i)
      ////tree.traverse[Int, ({ type l[A] = Const[List[Int], A] })#l](f)
      //tree.collapse assert_=== 10
      ////val function = BinaryTreeIsTraversable[Int].traverse(add)
      ////function(tree) must_== List(1)
      ////1 must_== 2
    //}
  //}
}
