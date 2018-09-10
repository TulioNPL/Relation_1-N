/*
 * Autores: Tulio N. Polido Lopes, Joao Victor da Silva, Gustavo Lescowicz Kotarsky, Temistocles Altivo Schwartz
 * Data: 21/08/2018
 * */


import java.io.*;
import java.util.Scanner;

public class Crud {

	private static RandomAccessFile arq;
	private static RandomAccessFile index;
	private static RandomAccessFile gen;
	private static Scanner input;
	private static int genID;

	public static void main(String[] args) {
		input = new Scanner(System.in);	
		int choice = -1;

		System.out.println("Bem-vindo ao CRUD de filmes!");
		try{	
			index = new RandomAccessFile("index.db","rw");
			arq = new RandomAccessFile("filme.db","rw");
			gen = new RandomAccessFile("generos.db","rw");

			int id;

			while(choice != 0) {
				System.out.println("---------------------------------\nMain Menu:\n"+
						"0 - Sair;\n"+
						"1 - Menu de Filmes;\n"+
						"2 - Menu de Generos\n------------------------------------");
				choice = input.nextInt();
				if(choice == 1) {
					while(choice != -1) { 
						System.out.println("-----------------------------------------------\nMenu de filmes:\n"+
								"0 - Voltar;\n"+
								"1 - Incluir filme;\n"+
								"2 - Alterar filme;\n"+
								"3 - Excluir filme;\n"+
								"4 - Consultar filme;\n-----------------------------------------------");
						choice = input.nextInt();

						switch(choice) {
							case 0:
								choice = -1;
								break;
							case 1:
								Filme filme = criarObjetoFilme();
								System.out.println("CRIADO O FILME = "+filme.getTitulo());

								if(filme != null) {
									create(filme,-1);
								}

								break;
							case 2:	
								System.out.println("Insira o ID do filme a ser alterado: ");
								id = input.nextInt();
								System.out.print("Deseja confirmar a alteração? Insira (1): ");
								if(input.nextByte() == 1) {
									update(id);
								}
								break;
							case 3:
								System.out.print("Insira o ID do filme a ser excluído: ");
								id = input.nextInt();
								System.out.print("Deseja confirmar a exclusão? Insira (1): ");

								if(input.nextByte() == 1) {
									delete(id);
								}

								break;
							case 4:
								System.out.print("Insira o ID do filme a ser pesquisado: ");
								id = input.nextInt();
								read(id);
								break;
							default:
								System.out.println("Opção inválida!");
								break;
						}
					}
				} else if (choice == 2) {
					while (choice != -1) {
						System.out.println("-------------------------------------\nMenu de Generos:\n"+
								"0 - Voltar;\n"+
								"1 - Inserir Genero;\n"+
								"2 - Alterar Genero;\n"+
								"3 - Excluir Genero;\n"+
								"4 - Consultar Genero;\n-----------------------------------");	
						choice = input.nextInt();
						switch(choice) {
							case 0 : 
								choice = -1;
								break;
							case 1:
								createGen(-1);
								break;
							case 2:
								updateGen();
								break;
							case 3: 
								deleteGen();
								break;
							case 4:
								readGen();
								break;

						}
					}
				} else {
					index.close();
					arq.close();
					gen.close();

				}
			}
		} catch (IOException ioException ) {
			ioException.printStackTrace();
		}

		System.out.println("Obrigado por utilizar o CRUD!");
	}//end main()

	public static void createGen(int idGen) {
		try {
			input = new Scanner(System.in);
			System.out.print("Digite o Genero: ");
			String genero = input.nextLine();

			if(searchGen(genero)) {
				System.out.println("Genero ja cadastrado!");
			} else {
				if(idGen == -1) {
					if(gen.length() == 0) {
						idGen = 0;
					} else {
						gen.seek(0);
						idGen = gen.readInt();
						idGen++;
					}
				}
				gen.seek(0);
				gen.writeInt(idGen);
				gen.seek(gen.length());
				gen.writeInt(idGen);
				gen.writeChar(' ');
				gen.writeUTF(genero);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateGen() {
		input = new Scanner(System.in);
		boolean continuar = true;
		System.out.print("Digite o ID do genero a ser alterado: ");
		int idGen = input.nextInt();

		try {
			gen.seek(4);
			while(gen.getFilePointer() < gen.length() && continuar) {
				if(idGen == gen.readInt()) {
					continuar = false;
					gen.writeChar('*');	
				} else {
					gen.readChar();
					gen.readUTF();
				}
			}
			if(!continuar) {
				createGen(idGen);		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void deleteGen() {
		input = new Scanner(System.in);

	}

	public static void readGen(){
		input = new Scanner(System.in);

	}

	public static boolean searchGen(String genero) {
		boolean resp = false;

		try {
			gen.seek(4);
			while(gen.getFilePointer() < gen.length() && !resp) {
				gen.readInt();
				gen.readChar();
				if(genero == gen.readUTF()) {
					resp = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resp;
	}

	/*
	 * Escreve o filme no arquivo
	 * @param uma instancia de filme a ser gravada
	 * @param id do filme a ser gravado
	 * */
	public static void create(Filme filme, int id){
		try { 
			if(id == -1) {
				if(arq.length() == 0) {
					id = 0;
				} else {
					arq.seek(0);
					id = arq.readInt();
					id++;
				}
			}
			arq.seek(0);
			arq.writeInt(id);
			arq.seek(arq.length());
			index.seek(searchIndex(id));
			index.writeInt(id);
			index.writeLong(arq.getFilePointer());
			filme.setId(id);
			filme.writeObject(arq);
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}//end create()

	/*
	 * Deleta o filme do arquivo(alterando a lapide
	 * @param id do filme a ser deletado
	 * */
	public static void delete(int id) {
		long pointArq = searchPointer(id);
		if(pointArq !=-1){

			try{
				arq.seek(pointArq);
				arq.writeChar('*');
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		else
			System.out.println("Filme não encontrado!");
	}//end delete()

	/*
	 * Altera as informacoes do filme selecionado
	 * @param id do filme a ser alterado
	 * */
	public static void update(int id){
		long pointArq = searchPointer(id);

		if(pointArq !=-1){

			try{
				arq.seek(pointArq);
				arq.writeChar('*');
				Filme filme = criarObjetoFilme();
				create(filme,id);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		else
			System.out.println("Filme não encontrado!");

	}//end update()

	/*
	 * Pesquisa as informacoes de um filme no arquivo
	 * @param id do filme a ser pesquisado
	 * */
	public static void read(int id){
		long pointerArq = searchPointer(id);
		System.out.println(pointerArq);

		if(pointerArq != -1){
			try{
				arq.seek(pointerArq);
				if(arq.readChar() != '*') {
					int tam = arq.readShort();

					byte[] registro = new byte[tam];

					for(short i = 0 ; i < tam; i++)
						registro[i] = arq.readByte();

					Filme filme  = new Filme();

					filme.setByteArray(registro);
					System.out.println(filme.toString());
				} else {
					System.out.println("Filme não encontrado!");
				}

			}catch(IOException e ){
				e.printStackTrace();
			}
		} else {
			System.out.println("Filme não encontrado!");
		} 		
	}//end read()

	/*
	 * Encontra o ponteiro do inicio de um registro
	 * @param id do registro cujo ponteiro eh desejado
	 * @return ponteiro do registro desejado
	 * */
	private static long searchPointer(int id) {
		int idLido;
		long address = -1;
		boolean continuar = true;

		try {
			index.seek(0);
			while((index.getFilePointer() < index.length()) && continuar) {
				idLido = index.readInt();
				address = index.readLong();
				if(id == idLido) {
					continuar = false;
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}

		return address;
	}//end searchPointer()

	/*
	 * Cria um objeto de filme com as informacoes da entrada
	 * @return uma instancia de Filme criada
	 * */
	public static Filme criarObjetoFilme(){
		Scanner input = new Scanner(System.in);
		String titulo,tituloOriginal,pais,diretor,sinopse,genero;
		short ano;
		short min;

		Filme filme = null;

		System.out.print("Titulo: ");
		titulo = input.nextLine();

		System.out.print("Titulo Original: ");
		tituloOriginal = input.nextLine();

		System.out.print("Pais de origem: ");
		pais = input.nextLine();

		System.out.print("Diretor: ");
		diretor = input.nextLine();

		System.out.print("Sinopse: ");
		sinopse = input.nextLine();

		System.out.print("Ano: ");
		ano = input.nextShort();

		System.out.print("Minutos filme: ");
		min = input.nextShort();

		System.out.print("Genero do filme: ");
		genero = input.nextLine();

		System.out.print("Insira 1 para confirma inclusão ou 0 para cancelar: ");
		if(input.nextByte() == 1) {
			filme = new Filme(titulo,tituloOriginal,pais,ano,min,diretor,sinopse,genero);
		}	
		return filme; 
	}//end criarObjetoFilme()

	private static long searchIndex(int id) {
		long address = -1;
		int idLido =1;
		boolean continuar = true;
		try {
			address = index.length();
			index.seek(0);
			while((index.getFilePointer() < index.length()) && continuar) {
				long aux = index.getFilePointer();
				idLido = index.readInt();
				index.readLong();
				if(idLido == id) {
					continuar = false;
					address = aux;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}
}//end Crud
