import customtkinter as ctk
from tkinter import filedialog, messagebox
from PIL import Image, ImageTk
import os
import pygame # Pour la musique

# --- Logique de base du traitement d'image (inchangée) ---
def supprimer_herbe_tileset_logic(image_path, output_path, couleurs_herbe_rgb, tolerance, status_callback=None, progress_callback=None):
    try:
        img = Image.open(image_path)
        if status_callback:
            status_callback(f"Traitement de: {os.path.basename(image_path)}...")
    except FileNotFoundError:
        if status_callback:
            status_callback(f"Erreur : Fichier non trouvé '{os.path.basename(image_path)}'.")
        return False
    except Exception as e:
        if status_callback:
            status_callback(f"Erreur ouverture {os.path.basename(image_path)}: {e}")
        return False

    img = img.convert("RGBA")
    donnees_pixels = img.getdata()
    nouvelles_donnees_pixels = []
    pixels_modifies = 0

    for i, pixel in enumerate(donnees_pixels):
        r, g, b, a = pixel
        est_pixel_herbe = False
        for hr, hg, hb in couleurs_herbe_rgb:
            if (abs(r - hr) <= tolerance and
                abs(g - hg) <= tolerance and
                abs(b - hb) <= tolerance):
                est_pixel_herbe = True
                break
        
        if est_pixel_herbe:
            nouvelles_donnees_pixels.append((r, g, b, 0))
            pixels_modifies += 1
        else:
            nouvelles_donnees_pixels.append(pixel)
    
    img.putdata(nouvelles_donnees_pixels)
    
    try:
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        img.save(output_path)
        if status_callback:
            status_callback(f"'{os.path.basename(output_path)}' sauvegardé ({pixels_modifies} pixels modifiés).")
        return True
    except Exception as e:
        if status_callback:
            status_callback(f"Erreur sauvegarde {os.path.basename(output_path)}: {e}")
        return False

# --- Classe principale de l'application GUI avec CustomTkinter ---
class GrassRemoverApp(ctk.CTk):
    def __init__(self):
        super().__init__()

        self.title("Suppresseur d'Herbe Avancé")
        self.geometry("950x800")

        ctk.set_appearance_mode("Dark") 
        ctk.set_default_color_theme("blue")

        try:
            pygame.mixer.init()
            self.music_loaded = False
            self.music_playing = False
            script_dir = os.path.dirname(os.path.abspath(__file__))
            music_file_path = os.path.join(script_dir, "background_music.mp3")
            if os.path.exists(music_file_path):
                pygame.mixer.music.load(music_file_path)
                self.music_loaded = True
            else:
                print(f"Fichier 'background_music.mp3' non trouvé dans {script_dir}. La musique ne sera pas disponible.")
        except Exception as e:
            print(f"Erreur initialisation Pygame Mixer: {e}. La musique ne sera pas disponible.")
            self.music_loaded = False
            self.music_playing = False

        self.input_path_type = ctk.StringVar(value="file")
        self.input_path = ctk.StringVar()
        self.output_path = ctk.StringVar()
        self.tolerance_var = ctk.IntVar(value=25)
        self.grass_colors_list = [] 

        self.grid_columnconfigure(0, weight=1) 
        self.grid_columnconfigure(1, weight=2) 
        self.grid_rowconfigure(0, weight=1)    

        controls_frame = ctk.CTkFrame(self, corner_radius=10)
        controls_frame.grid(row=0, column=0, padx=20, pady=20, sticky="nsew")
        controls_frame.grid_columnconfigure(0, weight=1)

        title_label = ctk.CTkLabel(controls_frame, text="Panneau de Contrôle", font=ctk.CTkFont(size=20, weight="bold"))
        title_label.grid(row=0, column=0, padx=10, pady=(10, 15), sticky="ew")

        input_type_frame = ctk.CTkFrame(controls_frame, fg_color="transparent")
        input_type_frame.grid(row=1, column=0, padx=10, pady=(0,10), sticky="ew")
        ctk.CTkLabel(input_type_frame, text="Type d'entrée:").pack(side=ctk.LEFT, padx=(0,10))
        self.radio_file = ctk.CTkRadioButton(input_type_frame, text="Fichier Unique", variable=self.input_path_type, value="file", command=self.on_input_type_change)
        self.radio_file.pack(side=ctk.LEFT, padx=5)
        self.radio_folder = ctk.CTkRadioButton(input_type_frame, text="Dossier Entier", variable=self.input_path_type, value="folder", command=self.on_input_type_change)
        self.radio_folder.pack(side=ctk.LEFT, padx=5)

        file_frame = ctk.CTkFrame(controls_frame, fg_color="transparent")
        file_frame.grid(row=2, column=0, padx=10, pady=5, sticky="ew")
        file_frame.grid_columnconfigure(1, weight=1)

        self.input_label = ctk.CTkLabel(file_frame, text="Image d'entrée:")
        self.input_label.grid(row=0, column=0, padx=(0,5), pady=5, sticky="w")
        ctk.CTkEntry(file_frame, textvariable=self.input_path, width=250).grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        self.browse_input_button = ctk.CTkButton(file_frame, text="Parcourir Fichier...", command=self.browse_input_file, width=130)
        self.browse_input_button.grid(row=0, column=2, padx=5, pady=5)

        self.output_label = ctk.CTkLabel(file_frame, text="Image de sortie:")
        self.output_label.grid(row=1, column=0, padx=(0,5), pady=5, sticky="w")
        ctk.CTkEntry(file_frame, textvariable=self.output_path, width=250).grid(row=1, column=1, padx=5, pady=5, sticky="ew")
        self.browse_output_button = ctk.CTkButton(file_frame, text="Enregistrer Fichier Sous...", command=self.browse_output_file, width=130)
        self.browse_output_button.grid(row=1, column=2, padx=5, pady=5)

        colors_outer_frame = ctk.CTkFrame(controls_frame, fg_color="transparent")
        colors_outer_frame.grid(row=3, column=0, padx=10, pady=10, sticky="ew")
        colors_outer_frame.grid_columnconfigure(0, weight=1)
        
        ctk.CTkLabel(colors_outer_frame, text="Couleurs de l'Herbe (RGB)", font=ctk.CTkFont(size=14, weight="bold")).grid(row=0, column=0, pady=(0,5), sticky="w")

        rgb_input_frame = ctk.CTkFrame(colors_outer_frame, fg_color="transparent")
        rgb_input_frame.grid(row=1, column=0, pady=5, sticky="ew")
        
        ctk.CTkLabel(rgb_input_frame, text="R:").pack(side=ctk.LEFT, padx=(0,2))
        self.r_var = ctk.StringVar()
        ctk.CTkEntry(rgb_input_frame, textvariable=self.r_var, width=40).pack(side=ctk.LEFT, padx=(0,5))
        ctk.CTkLabel(rgb_input_frame, text="G:").pack(side=ctk.LEFT, padx=(0,2))
        self.g_var = ctk.StringVar()
        ctk.CTkEntry(rgb_input_frame, textvariable=self.g_var, width=40).pack(side=ctk.LEFT, padx=(0,5))
        ctk.CTkLabel(rgb_input_frame, text="B:").pack(side=ctk.LEFT, padx=(0,2))
        self.b_var = ctk.StringVar()
        ctk.CTkEntry(rgb_input_frame, textvariable=self.b_var, width=40).pack(side=ctk.LEFT, padx=(0,10))
        ctk.CTkButton(rgb_input_frame, text="Ajouter", command=self.add_color, width=70).pack(side=ctk.LEFT, padx=5)

        self.colors_scrollable_frame = ctk.CTkScrollableFrame(colors_outer_frame, height=100)
        self.colors_scrollable_frame.grid(row=2, column=0, sticky="ew", padx=0, pady=5)
        self.color_widgets_in_scroll = [] 
        self.load_default_colors() # Appel pour charger les couleurs par défaut

        settings_frame = ctk.CTkFrame(controls_frame, fg_color="transparent")
        settings_frame.grid(row=4, column=0, padx=10, pady=10, sticky="ew")
        settings_frame.grid_columnconfigure(1, weight=1)

        ctk.CTkLabel(settings_frame, text="Tolérance Couleur:").grid(row=0, column=0, padx=(0,5), pady=5, sticky="w")
        self.tolerance_slider = ctk.CTkSlider(settings_frame, from_=0, to=255, variable=self.tolerance_var, command=self._update_tolerance_label)
        self.tolerance_slider.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        self.tolerance_label_value = ctk.CTkLabel(settings_frame, text=str(self.tolerance_var.get()), width=30)
        self.tolerance_label_value.grid(row=0, column=2, padx=5, pady=5, sticky="w")
        
        ctk.CTkLabel(settings_frame, text="Mode d'Apparence:").grid(row=1, column=0, padx=(0,5), pady=5, sticky="w")
        self.appearance_mode_menu = ctk.CTkOptionMenu(settings_frame, values=["Light", "Dark", "System"], command=self.change_appearance_mode)
        self.appearance_mode_menu.grid(row=1, column=1, columnspan=2, padx=5, pady=5, sticky="ew")
        self.appearance_mode_menu.set(ctk.get_appearance_mode())

        self.music_button = ctk.CTkButton(settings_frame, text="Musique: Play", command=self.toggle_music, width=120)
        self.music_button.grid(row=2, column=0, columnspan=3, padx=5, pady=10, sticky="ew")
        if not self.music_loaded:
            self.music_button.configure(state="disabled", text="Musique N/A")

        status_progress_frame = ctk.CTkFrame(controls_frame, fg_color="transparent")
        status_progress_frame.grid(row=5, column=0, padx=10, pady=(10,5), sticky="ew")
        status_progress_frame.grid_columnconfigure(0, weight=1)

        self.progress_bar = ctk.CTkProgressBar(status_progress_frame, orientation="horizontal", mode="determinate")
        self.progress_bar.set(0)
        self.progress_bar.grid(row=0, column=0, sticky="ew", padx=0, pady=(0,5))

        self.status_label = ctk.CTkLabel(status_progress_frame, text="Prêt.", wraplength=380, justify="left")
        self.status_label.grid(row=1, column=0, sticky="ew", padx=0, pady=0)
        
        self.process_button = ctk.CTkButton(controls_frame, text="Supprimer l'Herbe", command=self.process_image_or_folder, height=40, font=ctk.CTkFont(size=16, weight="bold"))
        self.process_button.grid(row=6, column=0, padx=10, pady=(20,10), sticky="ew")

        preview_outer_frame = ctk.CTkFrame(self, corner_radius=10)
        preview_outer_frame.grid(row=0, column=1, padx=(0,20), pady=20, sticky="nsew")
        preview_outer_frame.grid_rowconfigure(1, weight=1) 
        preview_outer_frame.grid_columnconfigure(0, weight=1)

        ctk.CTkLabel(preview_outer_frame, text="Aperçu de l'Image d'Entrée", font=ctk.CTkFont(size=16, weight="bold")).grid(row=0, column=0, padx=10, pady=10)
        
        self.image_preview_label = ctk.CTkLabel(preview_outer_frame, text="Aucune image chargée.\n\nSélectionnez une image en utilisant le bouton 'Parcourir...'", corner_radius=8, fg_color=("gray80", "gray25"))
        self.image_preview_label.grid(row=1, column=0, padx=10, pady=10, sticky="nsew")
        self.loaded_img_preview_ctk = None

        self.on_input_type_change() 
        
        script_dir = os.path.dirname(os.path.abspath(__file__))
        default_image_path = os.path.join(script_dir, "image.png")
        if os.path.exists(default_image_path) and self.input_path_type.get() == "file":
            self.input_path.set(os.path.abspath(default_image_path))
            self.after(100, self.update_image_preview) 
            base, ext = os.path.splitext(os.path.basename(self.input_path.get()))
            self.output_path.set(os.path.join(os.path.dirname(self.input_path.get()), f"{base}_sans_herbe{ext}"))
        
        self._update_tolerance_label(self.tolerance_var.get())
        self.protocol("WM_DELETE_WINDOW", self.on_closing)

    def on_input_type_change(self):
        if self.input_path_type.get() == "file":
            self.input_label.configure(text="Image d'entrée:")
            self.browse_input_button.configure(text="Parcourir Fichier...", command=self.browse_input_file)
            self.output_label.configure(text="Image de sortie:")
            self.browse_output_button.configure(text="Enregistrer Fichier Sous...", command=self.browse_output_file)
            if hasattr(self, 'image_preview_label'):
                 self.update_image_preview() 
        else: # folder
            self.input_label.configure(text="Dossier d'entrée:")
            self.browse_input_button.configure(text="Parcourir Dossier...", command=self.browse_input_folder)
            self.output_label.configure(text="Dossier de sortie:")
            self.browse_output_button.configure(text="Sélectionner Dossier Sortie...", command=self.browse_output_folder)
            if hasattr(self, 'image_preview_label'):
                self.loaded_img_preview_ctk = None
                self.image_preview_label.configure(image=None, text="Aperçu non disponible pour le traitement de dossier.")


    def _update_tolerance_label(self, value):
        self.tolerance_label_value.configure(text=str(int(float(value))))

    def update_color_listbox(self):
        for widget_info in self.color_widgets_in_scroll:
            widget_info["frame"].destroy()
        self.color_widgets_in_scroll = []

        for i, color in enumerate(self.grass_colors_list):
            color_frame = ctk.CTkFrame(self.colors_scrollable_frame, fg_color="transparent")
            color_frame.pack(fill="x", pady=2, padx=2)

            hex_color = f"#{color[0]:02x}{color[1]:02x}{color[2]:02x}"
            color_swatch = ctk.CTkLabel(color_frame, text="", width=20, height=20, fg_color=hex_color, corner_radius=3)
            color_swatch.pack(side=ctk.LEFT, padx=(2,5))
            
            label_text = f"R:{color[0]}, G:{color[1]}, B:{color[2]}"
            color_label = ctk.CTkLabel(color_frame, text=label_text)
            color_label.pack(side=ctk.LEFT, expand=True, fill="x", padx=2)

            delete_button = ctk.CTkButton(color_frame, text="✕", width=28, height=28,
                                          command=lambda c=color: self.remove_specific_color(c),
                                          fg_color="transparent", text_color=("#C0C0C0", "#505050"), hover_color=("#D32F2F", "#B71C1C"))
            delete_button.pack(side=ctk.RIGHT, padx=(5,2))
            
            self.color_widgets_in_scroll.append({"frame": color_frame, "color_tuple": color})

    def load_default_colors(self):
        # Liste de couleurs par défaut restaurée et enrichie
        default_colors = [
            (95, 151, 80),   # Vert clair typique
            (80, 125, 67),   # Vert plus foncé
            (103, 162, 88),  # Autre nuance de vert
            (61, 95, 50),    # Vert très foncé, proche du bord inférieur
            (115, 179, 98),  # Une autre nuance observée
            (78, 121, 64),   # Nuance restaurée
            (96, 148, 79),   # Nuance restaurée
            (110, 170, 90),  # Ajout d'une nuance intermédiaire
            (70, 110, 60)    # Ajout d'un vert olive/foncé
        ]
        for color in default_colors:
            if color not in self.grass_colors_list:
                self.grass_colors_list.append(color)
        self.update_color_listbox()

    def update_status(self, message):
        self.status_label.configure(text=message)
        self.update_idletasks() 

    def update_progress(self, value):
        self.progress_bar.set(value)
        self.update_idletasks()

    def browse_input_file(self):
        filepath = filedialog.askopenfilename(title="Sélectionner une image", filetypes=(("Images", "*.png;*.jpg;*.jpeg"), ("Tous les fichiers", "*.*")))
        if filepath:
            self.input_path.set(filepath)
            self.update_status(f"Image d'entrée: {os.path.basename(filepath)}")
            base, ext = os.path.splitext(os.path.basename(filepath))
            self.output_path.set(os.path.join(os.path.dirname(filepath), f"{base}_sans_herbe{ext}"))
            self.update_image_preview()
            
    def browse_input_folder(self):
        folderpath = filedialog.askdirectory(title="Sélectionner un dossier d'images source")
        if folderpath:
            self.input_path.set(folderpath)
            self.update_status(f"Dossier d'entrée: {os.path.basename(folderpath)}")
            self.output_path.set(os.path.join(folderpath, "processed_grass_removed"))
            if hasattr(self, 'image_preview_label'):
                self.loaded_img_preview_ctk = None
                self.image_preview_label.configure(image=None, text="Dossier sélectionné. Aperçu non disponible.")

    def update_image_preview(self):
        if self.input_path_type.get() == "folder":
            if hasattr(self, 'image_preview_label'):
                self.loaded_img_preview_ctk = None
                self.image_preview_label.configure(image=None, text="Aperçu non disponible pour le traitement de dossier.")
            return
        
        if not hasattr(self, 'image_preview_label'):
            self.after(50, self.update_image_preview) 
            return

        try:
            current_input_path = self.input_path.get()
            if not current_input_path or not os.path.exists(current_input_path) or os.path.isdir(current_input_path):
                self.loaded_img_preview_ctk = None 
                self.image_preview_label.configure(image=None, text="Aucune image chargée ou chemin invalide.")
                return

            pil_image = Image.open(current_input_path)
            self.image_preview_label.update_idletasks() 
            label_width = self.image_preview_label.winfo_width()
            label_height = self.image_preview_label.winfo_height()

            if label_width <= 1 or label_height <= 1: 
                self.after(50, self.update_image_preview)
                return

            img_copy = pil_image.copy()
            img_ratio = img_copy.width / img_copy.height
            label_ratio = label_width / label_height

            new_width = label_width if img_ratio > label_ratio else int(label_height * img_ratio)
            new_height = int(label_width / img_ratio) if img_ratio > label_ratio else label_height
            new_width, new_height = max(1, new_width), max(1, new_height)

            resized_image = img_copy.resize((new_width, new_height), Image.LANCZOS)
            
            self.loaded_img_preview_ctk = ctk.CTkImage(light_image=resized_image, dark_image=resized_image, size=(new_width, new_height))
            self.image_preview_label.configure(image=self.loaded_img_preview_ctk, text="")
        except Exception as e:
            if hasattr(self, 'image_preview_label'):
                self.loaded_img_preview_ctk = None 
                self.image_preview_label.configure(image=None, text=f"Erreur chargement aperçu:\n{str(e)[:100]}")
            self.update_status(f"Erreur lors du chargement de l'aperçu : {e}")

    def browse_output_file(self):
        initial_file = ""
        if self.input_path.get() and self.input_path_type.get() == "file":
            base, ext = os.path.splitext(os.path.basename(self.input_path.get()))
            initial_file = f"{base}_sans_herbe{ext}"
        initial_dir = os.path.dirname(self.input_path.get()) if self.input_path.get() else os.getcwd()
        filepath = filedialog.asksaveasfilename(title="Enregistrer l'image traitée sous...", defaultextension=".png", filetypes=(("Images PNG", "*.png"), ("Tous les fichiers", "*.*")), initialfile=initial_file, initialdir=initial_dir)
        if filepath:
            self.output_path.set(filepath)
            self.update_status(f"Chemin de sortie fichier: {os.path.basename(filepath)}")

    def browse_output_folder(self):
        folderpath = filedialog.askdirectory(title="Sélectionner le dossier de destination pour les images traitées")
        if folderpath:
            self.output_path.set(folderpath)
            self.update_status(f"Dossier de sortie: {os.path.basename(folderpath)}")

    def add_color(self):
        try:
            r_str, g_str, b_str = self.r_var.get(), self.g_var.get(), self.b_var.get()
            if not (r_str and g_str and b_str):
                messagebox.showwarning("Champs Vides", "Veuillez entrer les valeurs R, G et B.", parent=self)
                return
            r, g, b = int(r_str), int(g_str), int(b_str)
            if not (0<=r<=255 and 0<=g<=255 and 0<=b<=255): raise ValueError("Valeurs RGB entre 0-255.")
            color_tuple = (r,g,b)
            if color_tuple not in self.grass_colors_list:
                self.grass_colors_list.append(color_tuple)
                self.update_color_listbox()
                self.r_var.set(""); self.g_var.set(""); self.b_var.set("")
                self.update_status(f"Couleur ({r},{g},{b}) ajoutée.")
            else: messagebox.showwarning("Couleur Dupliquée", "Cette couleur est déjà dans la liste.", parent=self)
        except ValueError as e:
            messagebox.showerror("Erreur de Valeur", f"Valeur RGB invalide. {e}", parent=self)

    def remove_specific_color(self, color_to_remove):
        if color_to_remove in self.grass_colors_list:
            self.grass_colors_list.remove(color_to_remove)
            self.update_color_listbox()
            self.update_status(f"Couleur {color_to_remove} supprimée.")
        else:
            self.update_status(f"Erreur: Couleur {color_to_remove} non trouvée dans la liste.")

    def change_appearance_mode(self, new_appearance_mode: str):
        ctk.set_appearance_mode(new_appearance_mode)
        self.update_status(f"Mode d'apparence: {new_appearance_mode}")
        if hasattr(self, 'image_preview_label'):
            self.after(100, self.update_image_preview)

    def toggle_music(self):
        if not self.music_loaded:
            messagebox.showinfo("Musique non disponible", "Le fichier 'background_music.mp3' n'a pas pu être chargé.", parent=self)
            return
        try:
            if self.music_playing:
                pygame.mixer.music.stop()
                self.music_button.configure(text="Musique: Play")
                self.update_status("Musique arrêtée.")
            else:
                pygame.mixer.music.play(loops=-1)
                self.music_button.configure(text="Musique: Stop")
                self.update_status("Musique en cours...")
            self.music_playing = not self.music_playing
        except Exception as e:
            messagebox.showerror("Erreur Musique", f"Impossible de gérer la musique: {e}", parent=self)
            self.update_status(f"Erreur musique: {e}")

    def process_image_or_folder(self):
        in_path = self.input_path.get()
        out_path = self.output_path.get() 
        tolerance = self.tolerance_var.get()

        if not in_path: messagebox.showerror("Erreur d'Entrée", "Veuillez sélectionner un fichier ou dossier d'entrée.", parent=self); return
        if not out_path: messagebox.showerror("Erreur de Sortie", "Veuillez spécifier un chemin/dossier de sortie.", parent=self); return
        if not self.grass_colors_list: messagebox.showwarning("Aucune Couleur Cible", "Veuillez ajouter au moins une couleur d'herbe.", parent=self); return

        self.update_status("Préparation du traitement...")
        self.progress_bar.set(0)
        self.process_button.configure(state="disabled")
        self.after(10, lambda: self._execute_batch_processing(in_path, out_path, tolerance))

    def _execute_batch_processing(self, current_in_path, current_out_path, tolerance_val):
        is_folder_input = os.path.isdir(current_in_path)
        success_count = 0 

        if is_folder_input:
            if not os.path.isdir(current_out_path):
                try: os.makedirs(current_out_path, exist_ok=True)
                except Exception as e:
                    messagebox.showerror("Erreur Création Dossier", f"Impossible de créer le dossier de sortie: {e}", parent=self)
                    self.process_button.configure(state="normal"); return
            
            files_to_process = [f for f in os.listdir(current_in_path) if f.lower().endswith(('.png', '.jpg', '.jpeg'))]
            if not files_to_process:
                messagebox.showinfo("Aucune Image", "Aucune image compatible trouvée dans le dossier d'entrée.", parent=self)
                self.process_button.configure(state="normal"); self.update_status("Prêt."); return

            total_files = len(files_to_process)
            processed_count = 0
            
            for i, filename in enumerate(files_to_process):
                file_in_path = os.path.join(current_in_path, filename)
                file_out_path = os.path.join(current_out_path, filename) 
                if supprimer_herbe_tileset_logic(file_in_path, file_out_path, self.grass_colors_list, tolerance_val, self.update_status):
                    success_count +=1
                processed_count += 1
                self.progress_bar.set(processed_count / total_files)
                self.update_idletasks() 

            self.update_status(f"Traitement par lot terminé. {success_count}/{total_files} images traitées avec succès.")
            if success_count > 0 and messagebox.askyesno("Traitement Terminé", f"{success_count}/{total_files} images traitées.\nVoulez-vous ouvrir le dossier de sortie ?", parent=self):
                self._open_explorer(current_out_path)
        else: 
            if supprimer_herbe_tileset_logic(current_in_path, current_out_path, self.grass_colors_list, tolerance_val, self.update_status, self.update_progress):
                success_count = 1 
                if messagebox.askyesno("Traitement Terminé", f"L'image a été sauvegardée.\nVoulez-vous ouvrir le dossier la contenant ?", parent=self):
                    self._open_explorer(os.path.dirname(current_out_path))
            else: self.update_status("Échec du traitement de l'image.")

        self.process_button.configure(state="normal")
        if not (is_folder_input and success_count > 0) and not (not is_folder_input and success_count > 0) :
             self.update_status("Prêt.")


    def _open_explorer(self, path):
        try:
            if os.name == 'nt': os.startfile(path)
            elif hasattr(os, 'uname') and os.uname().sysname == 'Darwin': os.system(f'open "{path}"')
            else: os.system(f'xdg-open "{path}"')
        except Exception as e: self.update_status(f"Impossible d'ouvrir le dossier : {e}")

    def on_closing(self):
        if hasattr(self, 'music_playing') and self.music_playing:
            try: pygame.mixer.music.stop()
            except Exception: pass 
        try: pygame.quit()
        except Exception: pass
        self.destroy()

if __name__ == "__main__":
    app = GrassRemoverApp()
    app.mainloop()