/*
 * Autores: Tulio N. Polido Lopes, Joao Victor da Silva, Gustavo Lescowicz Kotarsky, Temistocles Altivo Schwartz
 * Data: 21/08/2018
 * */


import java.io.*;
import java.util.Scanner;

public class Crud {

	private static RandomAccessFile arq;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);	
		int choice = -1;

		System.out.println("Bem-vindo ao CRUD de filmes!");
		try{	
			arq = new RandomAccessFile("filme.db","rw");

			int id;

			while(choice != 0) { 
				System.out.println("-----------------------------------------------\nMenu:\n"+
						"0 - Sair;\n"+
						"1 - Incluir filme;\n"+
						"2 - Alterar filme;\n"+
						"3 - Excluir filme;\n"+
						"4 - Consultar filme;\n-----------------------------------------------");
				choice = input.nextInt();

				switch(choice) {
					case 0:
						arq.close();
						System.out.println("Obrigado por utilizar o CRUD de filmes!");
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
		} catch (IOException ioException ) {
			ioException.printStackTrace();
		}
	}//end main()

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
	public static void delete(int id){
		long pointArq = searchPointer(id);
		if(pointArq !=0){

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

		if(pointArq !=0){

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

		if(pointerArq != 0){
			try{
				arq.seek(pointerArq);
				arq.skipBytes(2);

				int tam = arq.readShort();

				byte[] registro = new byte[tam];

				for(short i = 0 ; i < tam; i++)
					registro[i] = arq.readByte();

				Filme filme  = new Filme();

				filme.setByteArray(registro);
				System.out.println(filme.toString());

			}catch(IOException e ){
				e.printStackTrace();
			}
		}
		else
			System.out.println("Filme não encontrado!");		
	}//end read()

	/*
	 * Encontra o ponteiro do inicio de um registro
	 * @param id do registro cujo ponteiro eh desejado
	 * @return ponteiro do registro desejado
	 * */
	private static long searchPointer(int id){

		long pointArq = 0;
		long tamArquivo;
		boolean continuar = true;

		try{
			tamArquivo = arq.length();

			if(tamArquivo == 0)
				System.out.println("ERRO : Arquivo vazio!");
			else{
				arq.seek(4);
				pointArq = arq.getFilePointer();
				while(continuar & pointArq < tamArquivo){

					char lapide = arq.readChar();

					short tamRegistro = arq.readShort();

					if(lapide != '*' && arq.readInt() == id )
						continuar = false;
					else{
						arq.seek(pointArq);
						arq.skipBytes(tamRegistro+4);
						pointArq = arq.getFilePointer();

					}	
				}
			}	
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return continuar?0:pointArq;

	}//end searchPointer()

	/*
	 * Cria um objeto de filme com as informacoes da entrada
	 * @return uma instancia de Filme criada
	 * */
	public static Filme criarObjetoFilme(){
		Scanner input = new Scanner(System.in);
		String titulo,tituloOriginal,pais,diretor,sinopse;
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

		System.out.print("Insira 1 para confirma inclusão ou 0 para cancelar: ");
		if(input.nextByte() == 1) {
			filme = new Filme(titulo,tituloOriginal,pais,ano,min,diretor,sinopse);
		}	
		return filme; 
	}//end criarObjetoFilme()
}//end Crud
