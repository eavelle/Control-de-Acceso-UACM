# Usar tu teléfono como lector de QR o código de barras

Esta guía sirve para probar el escaneo de credenciales **sin comprar un lector físico**. El
teléfono lee el código y lo escribe en la computadora como si fuera un teclado.

Para Android, la opción recomendada es **software libre**: no tiene anuncios, no requiere
conexión a internet y no necesita instalar nada en la computadora.

## Lo que necesitas

- La demo corriendo en `http://localhost:8080`.
- Un teléfono Android 9 o superior con cámara.
- Bluetooth en la computadora y en el teléfono.
- La hoja de códigos de prueba:
  - [`credenciales-qr.html`](credenciales-qr.html) para verla en pantalla o imprimirla con
    `Ctrl + P`.
  - [`credenciales-qr.pdf`](credenciales-qr.pdf) lista para imprimir en tamaño A4.

> El contenido de cada QR es únicamente el código de la credencial, por ejemplo
> `UACM-20260001`. No es una URL.

---

## Opción A — Bluetooth QR & Barcode to PC (Android, software libre, recomendada)

Esta app convierte el teléfono en un **teclado Bluetooth**. Escanea el código y lo escribe en la
computadora, sin servidor ni cables.

- **Nombre:** Bluetooth QR & Barcode to PC (la app se identifica como *BT Scanner*).
- **Autor:** fabik.
- **Licencia:** GNU GPL v3.0.
- **Código fuente:** <https://github.com/Fabi019/hid-barcode-scanner>
- **Descarga libre:** <https://f-droid.org/packages/dev.fabik.bluetoothhid/>
- **Requisitos:** Android 9 o superior con soporte de Bluetooth HID (la mayoría de los teléfonos).
- **Ventajas:** gratuita, sin anuncios, sin compras dentro de la app, funciona sin internet y no
  necesita software en la PC.

> También está en Google Play, pero es la misma app con otra firma. Si ya la instalaste desde
> Play Store y quieres cambiar a F-Droid, primero desinstálala: las firmas son distintas y
> Android no permite actualizar entre ellas.

### 1. Instalar la app

1. Abre <https://f-droid.org/packages/dev.fabik.bluetoothhid/> en el teléfono e instala la app.
   También puedes buscarla en F-Droid como **Bluetooth QR & Barcode to PC**.
2. Abre la app y concede los permisos de **cámara** y **dispositivos cercanos** (Bluetooth).
   Si también pide **ubicación**, concédela: Android la exige para buscar dispositivos Bluetooth;
   la app no la usa para rastrearte.

### 2. Emparejar el teléfono con la computadora

1. Activa Bluetooth en la computadora y en el teléfono. Asegúrate de que la computadora sea
   visible para otros dispositivos Bluetooth.
2. En la app, abre **Devices** y busca el nombre de tu computadora.
3. Toca la computadora. Debe aparecer una solicitud de emparejamiento; acéptala en ambos
   equipos.
4. Si la computadora ya estaba emparejada con el teléfono desde Windows y la conexión falla,
   desempareja en **ambos** lados y vuelve a emparejar desde la app. Es importante que la
   computadora conozca al teléfono como teclado Bluetooth.

> La app incluye una prueba llamada **Bluetooth HID Profile Tester** en Google Play. Si esa
> prueba falla, tu teléfono no soporta el perfil HID y debes usar la opción B.

### 3. Configurar la app

Abre **Settings** y ajusta:

| Ajuste | Valor | Para qué sirve |
|---|---|---|
| **Keyboard layout** | **English (US)** en esta computadora | Que las letras y símbolos coincidan con el teclado de Windows. |
| **Auto-send** | Activado | Envía el código apenas lo detecta, sin tocar la pantalla. |
| **Extra keys** | **Return** | Agrega **Enter** al final, para que el formulario se envíe solo. |
| **Connection Mode** | **HID Keyboard** | Modo teclado Bluetooth (el predeterminado). |

> **Importante:** esta computadora usa **Español (México)**, que tiene la misma posición física de
> teclas que un teclado **inglés (EE. UU.)**. Por eso en la app debes elegir **English (US)**, no
> **Spanish (ES)**. Si eliges Spanish (ES), el guion `-` se envía como apóstrofo `'` y el código
> `UACM-20260003` llega como `UACM'20260003`; la credencial no se reconoce.
>
> Si algún día cambias Windows a **Español (España)**, entonces sí elige **Spanish (ES)** en la app.

### 4. Escanear

1. En la computadora, entra a `http://localhost:8080` con `guardia / Guardia123!`.
2. Abre **Revisar identidad**.
3. Haz clic en el campo **“Coloca la credencial en el lector”**.
4. En el teléfono, abre la app y apunta al QR de una credencial.
5. El código se escribirá en el campo y se enviará con **Enter**. La tarjeta de identidad
   aparecerá en pantalla.

> Mientras escaneas, no cambies de ventana en la computadora: la app escribe en la ventana que
> tenga el foco.

> **Prueba rápida:** abre el **Bloc de notas** y escanea un QR. Debe escribir el código exacto,
> por ejemplo `UACM-20260003`. Si aparece `UACM'20260003`, el **Keyboard layout** de la app no
> coincide con el de Windows; cámbialo antes de probar en el navegador.

---

## Opción B — Barcode to PC (Wi-Fi, Android o iPhone)

Úsala solo si el Bluetooth HID no funciona en tu teléfono o si prefieres no emparejar por
Bluetooth. Esta app es **propietaria** y su plan gratuito tiene un límite de escaneos, pero
requiere menos pasos en algunos equipos.

### 1. Instalar el servidor en la computadora

1. Abre <https://barcodetopc.com/>.
2. Descarga e instala **Barcode to PC Server** para Windows.
3. Abre el servidor. Si Windows pregunta si permites el acceso a la red, elige **Red privada**.
4. Verifica que la opción **Keyboard Emulation** esté activada en la configuración del servidor.

### 2. Instalar la app en el teléfono

1. Busca **Barcode to PC: Wi-Fi scanner** en Google Play (Android) o en App Store (iPhone).
2. Instálala y abre la app.
3. Conecta el teléfono a la misma red Wi-Fi que la computadora.
4. La app debería detectar el servidor automáticamente. Si no aparece, escribe la dirección IP
   que muestra el servidor en la computadora.

### 3. Escanear

1. En la computadora, entra a `http://localhost:8080` con `guardia / Guardia123!`.
2. Abre **Revisar identidad**.
3. Haz clic en el campo **“Coloca la credencial en el lector”**.
4. En el teléfono, toca el botón de la cámara y apunta al QR de una credencial.
5. El código se escribirá en el campo. Si la app no lo envía sola, presiona **Enter** en la
   computadora.
6. El resultado del acceso aparecerá en pantalla: **Permitido** o **Denegado**, junto con la
   identidad de la persona.

---

## Prueba rápida

Escanea estos códigos de la hoja, en este orden:

| QR | Lo que debe aparecer |
|---|---|
| `UACM-20260001` | **Acceso permitido** · Ana López García · Activo · Credencial Activa |
| `UACM-20260003` | **Acceso denegado** · Luis Hernández Soto · Con prohibición de acceso · botón **Permitir con advertencia** |
| `UACM-20260004` | **Acceso denegado** · Sofía Torres Vega · Credencial Vencida · botón **Permitir con advertencia** |
| `NO-EXISTE` | **Acceso denegado** · credencial no reconocida · sin botón de autorización |

Después entra a **Bitácora** y filtra por **Acceso permitido** o **Acceso denegado**: cada
escaneo debe aparecer con el nombre de la persona y el resultado registrado.

---

## Problemas comunes

| Problema | Qué revisar |
|---|---|
| No se escribe nada por Bluetooth | La app no está conectada a la PC, el teléfono no soporta HID o se emparejó primero desde Windows. Desempareja en ambos lados y empareja desde la app. |
| No se escribe nada por Wi-Fi | El servidor no está abierto, el teléfono y la PC están en redes distintas, hay una VPN activa o la red Wi-Fi es de invitados. |
| El código aparece pero no se envía | En la app, pon **Extra keys** en **Return**. Si no, presiona **Enter** en la computadora. |
| Al escanear sale `UACM'20260003` (apóstrofo en vez de guion) | Windows usa **Español (México)** y la app está en **Spanish (ES)**. Cambia **Keyboard layout** a **English (US)**. |
| Se escribe dos veces o faltan caracteres | Aumenta **Transmit delay** en la app, limita los formatos a **QR** y acerca la cámara. |
| La app no encuentra la computadora | Empareja desde la app, no desde Windows; reinicia la app o apaga y enciende Bluetooth. |
| La PC no acepta el teléfono como teclado | Prueba con **Bluetooth HID Profile Tester**. Si falla, usa la opción B. |
| El navegador no recibe el texto | Haz clic en el campo del lector antes de escanear; si cambiaste de ventana, el foco se perdió. |

---

## Sobre el software libre

**Bluetooth QR & Barcode to PC** es software libre con licencia **GPL-3.0**. Puedes revisar y
modificar su código en <https://github.com/Fabi019/hid-barcode-scanner>. No tiene anuncios, no
recopila datos y funciona completamente sin internet.

**Barcode to PC** es propietaria. Su plan gratuito permite 300 escaneos en total al momento de
preparar esta guía, suficiente para la demostración, pero no es software libre.

Para la entrega final, la opción más sencilla sigue siendo un lector USB físico: funciona sin
instalar nada y el sistema ya lo acepta como teclado.
