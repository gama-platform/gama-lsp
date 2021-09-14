run:
	mvn package
	java -cp gama.core.lang.ide/target/gama.core.lang.ide-1.0.0-SNAPSHOT-ls.jar gama.core.lang.RunLSP $(ARGS)

run2:
	java -cp gama.core.lang.ide/target/gama.core.lang.ide-1.0.0-SNAPSHOT-ls.jar gama.core.lang.RunLSP $(ARGS)

package:
	mvn package

